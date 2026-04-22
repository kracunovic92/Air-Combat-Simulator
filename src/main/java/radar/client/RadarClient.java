package radar.client;

import common.Position;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import radar.FlyingObjectType;
import radar.RadarContact;
import radar.RadarProtoMapper;
import radar.RadarScanResult;
import radar.grpc.*;

import java.util.ArrayList;
import java.util.List;

public class RadarClient {

    private final ManagedChannel channel;
    private final RadarGrpcServiceGrpc.RadarGrpcServiceBlockingStub blockingStub;

    public RadarClient(String host, int port){
        this.channel = ManagedChannelBuilder.forAddress(host,port).usePlaintext().build();
        this.blockingStub = RadarGrpcServiceGrpc.newBlockingStub(channel);

    }

    public RadarScanResult reportAndScan(String id, FlyingObjectType type, Position position, double radarRange, String targetId) {

        ReportAndScanRequest.Builder builder = ReportAndScanRequest.newBuilder()
                .setId(id)
                .setType(RadarProtoMapper.toProto(type))
                .setPosition(RadarProtoMapper.toProto(position))
                .setRadarRange(radarRange);

        if (targetId != null && !targetId.isBlank()) {
            builder.setTargetId(targetId);
        }

        ReportAndScanResponse response = blockingStub.reportAndScan(builder.build());
        List<RadarContact> contacts = new ArrayList<>();
        for (RadarContactMessage c : response.getContactsList()) {
            contacts.add(RadarProtoMapper.fromProto(c));
        }

        String hitTargetId = response.getHitTargetId().isBlank()
                ? null
                : response.getHitTargetId();

        return new RadarScanResult(contacts, response.getSelfDestroyed(), response.getHitConfirmed(), hitTargetId);
    }

    public boolean removeTrackedObject(String id) {
        RemoveTrackedObjectRequest request = RemoveTrackedObjectRequest.newBuilder()
                .setId(id)
                .build();

        RemoveTrackedObjectResponse response = blockingStub.removeTrackedObject(request);
        return response.getRemoved();
    }
    public void shutdown(){

        if(channel != null && !channel.isShutdown()){
            channel.shutdown();
        }
    }
}
