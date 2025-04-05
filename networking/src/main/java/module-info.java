module transportAgency.networking {
    exports transportAgency.proto;
    exports transportAgency.rpcprotocol;
    exports transportAgency.dto;
    requires java.sql;
    requires transportAgency.model;
    requires transportAgency.services;
    requires com.google.protobuf;
}