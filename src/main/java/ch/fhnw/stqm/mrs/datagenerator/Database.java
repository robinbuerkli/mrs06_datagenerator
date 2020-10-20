package ch.fhnw.stqm.mrs.datagenerator;

import javax.sql.DataSource;

public interface Database {
	DataSource getDataSource();
}
