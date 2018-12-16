package com.tomoyadeng.trpc.core.registry;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.tomoyadeng.trpc.core.common.EndPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {
    private static final String ROOT_PATH = "trpc";
    private static final String DEFAULT_ADDRESS = "http://127.0.0.1:2379";
    private static final int LEASE_TTL = 60;

    private String registryAddress;
    private Lease lease;
    private KV kv;
    private long leaseId;

    public EtcdRegistry() {
        this(DEFAULT_ADDRESS);
    }

    public EtcdRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
        Client client = Client.builder().endpoints(this.registryAddress).build();
        this.lease = client.getLeaseClient();
        this.kv = client.getKVClient();
        try {
            this.leaseId = lease.grant(LEASE_TTL).get().getID();
        } catch (InterruptedException | ExecutionException e) {
            log.error("init etcd registry exception", e);
        }

        keepAlive();
    }

    private void keepAlive() {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Lease.KeepAliveListener listener = lease.keepAlive(leaseId);
                listener.listen();
                log.info("KeepAlive lease:" + leaseId + "; Hex format:" + Long.toHexString(leaseId));
            } catch (InterruptedException e) {
                log.error("KeepAlive lease disconnected,leaseId : " + leaseId, e);
            }
        });
    }

    @Override
    public void register(EndPoint endPoint, String serviceName) throws Exception {
        String strKey = String.format("/%s/%s/%s:%s", ROOT_PATH, serviceName, endPoint.getHost(), endPoint.getPort());
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString("");

        kv.put(key,val, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        log.info("Register a new service at : {}", strKey);
    }

    @Override
    public List<EndPoint> discovery(String serviceName) throws Exception {
        String strKey = String.format("/%s/%s", ROOT_PATH, serviceName);
        ByteSequence key = ByteSequence.fromString(strKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();
        List<EndPoint> endPoints = response.getKvs()
                .stream()
                .map(this::toEndPoint)
                .collect(Collectors.toList());
        log.info("[discovery] {}: {}", serviceName, endPoints);
        return endPoints;
    }

    private EndPoint toEndPoint(KeyValue keyValue) {
        String s = keyValue.getKey().toStringUtf8();
        String endPointStr = s.substring(s.lastIndexOf("/") + 1);
        String[] endPointArr = endPointStr.split(":");
        return new EndPoint(endPointArr[0], Integer.parseInt(endPointArr[1]));
    }
}
