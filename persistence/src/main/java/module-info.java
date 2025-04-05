module transportAgency.persistence {
    exports transportAgency.persistence;
    exports transportAgency.persistence.jdbc;
    requires transportAgency.model;
    requires org.apache.logging.log4j;
    requires java.sql;
}