module transportAgency.networking {
    exports transportAgency.dto;
    exports transportAgency.utils;
    exports transportAgency.objectprotocol;
    requires transportAgency.model;
    requires transportAgency.services;
    requires org.apache.logging.log4j;
    requires java.sql;
}