module transportAgency.networking {
    exports transportAgency.dto;
    exports transportAgency.utils;
    exports transportAgency.objectprotocol;
    requires java.sql;
    requires transportAgency.model;
    requires transportAgency.services;
    requires com.google.protobuf;
}