package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.response;

public sealed interface Response permits HandshakeResponse, StateResponse, EventResponse, ErrorResponse {

}
