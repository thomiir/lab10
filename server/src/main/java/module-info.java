module transportAgency.server {
    requires transportAgency.persistence;
    requires transportAgency.networking;
    requires transportAgency.services;
    requires transportAgency.model;
    requires org.apache.logging.log4j;
    requires java.sql;
}