package radar.server;

import common.Position;
import io.grpc.stub.StreamObserver;
import radar.*;
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
            RadarScanResult result = radarService.reportAndScan(
                    request.getId(),
                    RadarProtoMapper.fromProto(request.getType()),
                    RadarProtoMapper.fromProto(request.getPosition()),
                    request.getRadarRange(),
                    request.getTargetId()
            );

            ReportAndScanResponse.Builder responseBuilder = ReportAndScanResponse.newBuilder()
                    .setSelfDestroyed(result.selfDestroyed())
                    .setHitConfirmed(result.hitConfirmed());

            if (result.hitTargetId() != null) {
                responseBuilder.setHitTargetId(result.hitTargetId());
            }

            for (RadarContact contact : result.contacts()) {
                responseBuilder.addContacts(RadarProtoMapper.toProto(contact));
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTrackedObject(
            RemoveTrackedObjectRequest request,
            StreamObserver<RemoveTrackedObjectResponse> responseObserver
    ) {
        try {
            boolean removed = radarService.removeTrackedObject(request.getId());

            RemoveTrackedObjectResponse response = RemoveTrackedObjectResponse.newBuilder()
                    .setRemoved(removed)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }


}
