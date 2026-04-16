package radar;


import common.Position;
import radar.FlyingObjectType;
import radar.RadarContact;
import radar.grpc.FlyingObjectTypeMessage;
import radar.grpc.PositionMessage;
import radar.grpc.RadarContactMessage;

public final class RadarProtoMapper {

    private RadarProtoMapper() {
    }

    public static PositionMessage toProto(Position position) {
        return PositionMessage.newBuilder()
                .setX(position.column())
                .setY(position.row())
                .build();
    }

    public static Position fromProto(PositionMessage message) {
        return new Position(message.getX(), message.getY());
    }

    public static FlyingObjectTypeMessage toProto(FlyingObjectType type) {
        return switch (type) {
            case AIRCRAFT -> FlyingObjectTypeMessage.AIRCRAFT;
            case MISSILE -> FlyingObjectTypeMessage.MISSILE;
        };
    }

    public static FlyingObjectType fromProto(FlyingObjectTypeMessage type) {
        return switch (type) {
            case AIRCRAFT -> FlyingObjectType.AIRCRAFT;
            case MISSILE -> FlyingObjectType.MISSILE;
            case FLYING_OBJECT_TYPE_UNSPECIFIED, UNRECOGNIZED ->
                    throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    public static RadarContactMessage toProto(RadarContact contact) {
        return RadarContactMessage.newBuilder()
                .setId(contact.id())
                .setType(toProto(contact.type()))
                .setPosition(toProto(contact.position()))
                .setDistance(contact.distance())
                .build();
    }

    public static RadarContact fromProto(RadarContactMessage message) {
        return new RadarContact(
                message.getId(),
                fromProto(message.getType()),
                fromProto(message.getPosition()),
                message.getDistance()
        );
    }
}