package com.contentful.java.cda.integration;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAHttpException;
import com.contentful.java.cda.CDAResourceNotFoundException;
import com.contentful.java.cda.CDASpace;

import org.junit.Test;

import static com.contentful.java.cda.CDAType.SPACE;
import static com.contentful.java.cda.QueryOperation.IsEarlierOrAt;
import static com.contentful.java.cda.QueryOperation.IsEarlierThan;
import static com.google.common.truth.Truth.assertThat;

public class IntegrationWithStagingEnvirnoment extends IntegrationWithMasterEnvironment {
  public void setUp() throws Exception {
    client = CDAClient.builder()
        .setSpace("5s4tdjmyjfpl")
        .setToken("16bc99120b1e0b33cc16323324baab4dbe9350c0d7a2b222ed8d7d5328e95bf3")
        .setEnvironment("staging")
        .build();
  }


  // cannot fetch space on master
  @Test(expected = CDAHttpException.class)
  @Override public void fetchSpace() throws Exception {
    CDASpace space = client.fetchSpace();
    assertThat(space.name()).isEqualTo("Contentful Example API with En");
    assertThat(space.id()).isEqualTo("5s4tdjmyjfpl");
    assertThat(space.type()).isEqualTo(SPACE);
  }

  // cannot sync on non master
  @Test(expected = IllegalStateException.class)
  @Override
  public void sync() throws Exception {
    super.sync();
  }

  // cannot sync on non master
  @Test(expected = IllegalStateException.class)
  @Override
  public void syncWithTypes() throws Exception {
    super.syncWithTypes();
  }


  // cannot sync on non master
  @Test(expected = IllegalStateException.class)
  @Override
  public void testRawFields() throws Exception {
    super.testRawFields();
  }

  @Override
  public void fetchEntriesInRange() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsEarlierThan, "1980-01-01")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry nyancat = (CDAEntry) found.items().get(0);
    assertThat(nyancat.getField("name")).isEqualTo("Staged Garfield");
  }

  //"/spaces/{space_id}/entries?content_type={content_type}&{attribute}%5Blte%5D={value}",
  @Override
  public void fetchEntriesEarlierOrAt() {
    CDAArray found = client.fetch(CDAEntry.class)
        .withContentType("cat")
        .where("fields.birthday", IsEarlierOrAt, "1979-06-18T23:00:00")
        .all();

    assertThat(found.items().size()).isEqualTo(1);
    CDAEntry cat = (CDAEntry) found.items().get(0);
    assertThat(cat.getField("name")).isEqualTo("Staged Garfield");
  }

}
