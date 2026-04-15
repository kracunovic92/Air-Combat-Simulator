package radar.server;

import common.Position;
import io.grpc.stub.StreamObserver;
import radar.FlyingObjectType;
import radar.IRadarService;
import radar.RadarContact;
import radar.RadarProtoMapper;
import radar.grpc.*;

import java.util.List;

public class RadarGrpcServiceImpl extends RadarGrpcServiceGrpc.RadarGrpcServiceImplBase {

    private final IRadarService radarService;

    public RadarGrpcServiceImpl(IRadarService radarService){
        this.radarService = radarService;
    }
    @Override
    public void reportAndScan(
            ReportAndScanRequest request,
            StreamObserver<ReportAndScanResponse> responseObserver
    ){
        try {
            List<RadarContact> contacts = radarService.reportAndScan(
                    request.getId(),
                    RadarProtoMapper.fromProto(request.getType()),
                    RadarProtoMapper.fromProto(request.getPosition()),
                    request.getRadarRange()
            );

            ReportAndScanResponse.Builder responseBuilder = ReportAndScanResponse.newBuilder();

            for (RadarContact contact : contacts) {
                responseBuilder.addContacts(RadarProtoMapper.toProto(contact));
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
