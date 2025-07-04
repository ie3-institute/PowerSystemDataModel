import edu.ie3.datamodel.models.profile.LoadProfileProvider;

open module PowerSystemDataModel.main {
  // our own projects
  requires PowerSystemUtils;

  // dependencies
  requires com.couchbase.client.core;
  requires com.couchbase.client.java;
  requires elki.core.math;
  requires elki.core.util;
  requires influxdb.java;
  requires java.desktop;
  requires java.measure;
  requires java.sql;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.jgrapht.core;
  requires org.locationtech.jts;
  requires org.locationtech.jts.io;
  requires org.slf4j;
  requires tech.units.indriya;

  // used to provide load profiles
  uses LoadProfileProvider;

  // provide default load profiles to profile loader
  provides LoadProfileProvider with
      LoadProfileProvider.DefaultLoadProfileProvider;
}
