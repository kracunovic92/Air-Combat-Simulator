package radar.client;

import common.Position;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import radar.FlyingObjectType;
import radar.RadarContact;
import radar.RadarProtoMapper;
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

    public List<RadarContact> reportAndScan(String id, FlyingObjectType type, Position position, double radarRange) {

        ReportAndScanRequest request = ReportAndScanRequest.newBuilder()
                .setId(id)
                .setType(RadarProtoMapper.toProto(type))
                .setPosition(RadarProtoMapper.toProto(position))
                .setRadarRange(radarRange)
                .build();

        ReportAndScanResponse response = blockingStub.reportAndScan(request);

        List<RadarContact> contacts = new ArrayList<>();
        for (RadarContactMessage c : response.getContactsList()) {
            contacts.add(RadarProtoMapper.fromProto(c));
        }

        return contacts;
    }
}
