/*
 * Copyright © 2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.plugin.gcp.bigtable.sink;

import com.google.bigtable.repackaged.com.google.cloud.ServiceOptions;
import io.cdap.cdap.etl.api.validation.InvalidConfigPropertyException;
import io.cdap.plugin.common.Constants;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class BigtableSinkConfigTest {
  private static final String VALID_REF = "test-ref";
  private static final String VALID_TABLE = "test-table";
  private static final String VALID_INSTANCE = "test-instance";
  private static final String VALID_PROJECT = "test-project";
  private static final String VALID_ACCOUNT_FILE_PATH =
    BigtableSinkConfig.class.getResource("/credentials.json").getPath();
  private static final String VALID_KEY_ALIAS = "test-alias";
  private static final String VALID_COLUMN_MAPPING = "test-family:id=id";
  private static final String VALID_BIGTABLE_OPTIONS = "";

  @Test
  public void testValidateValidConfig() {
    BigtableSinkConfig config = getBuilder()
      .build();

    config.validate(null);
  }

  @Test
  @Ignore
  public void testValidateReference() {
    BigtableSinkConfig config = getBuilder()
      .setReferenceName("")
      .build();

    // TODO: (vinisha) validate failure instead of stage config once this method is migrated to new api
    validateConfigValidationFail(config, Constants.Reference.REFERENCE_NAME);
  }

  @Test
  public void testValidateMissingTable() {
    BigtableSinkConfig config = getBuilder()
      .setTable(null)
      .build();

    validateConfigValidationFail(config, BigtableSinkConfig.TABLE);
  }

  @Test
  public void testValidateMissingInstanceId() {
    BigtableSinkConfig config = getBuilder()
      .setInstance(null)
      .build();

    validateConfigValidationFail(config, BigtableSinkConfig.INSTANCE);
  }

  @Test
  public void testValidateMissingProjectId() {
    Assume.assumeTrue(ServiceOptions.getDefaultProjectId() == null);

    BigtableSinkConfig config = getBuilder()
      .setProject(null)
      .build();

    validateConfigValidationFail(config, BigtableSinkConfig.NAME_PROJECT);
  }

  @Test
  public void testValidateMissingCredentialsFile() {
    BigtableSinkConfig config = getBuilder()
      .setServiceFilePath("/tmp/non_existing_file")
      .build();

    validateConfigValidationFail(config, BigtableSinkConfig.NAME_SERVICE_ACCOUNT_FILE_PATH);
  }

  private static BigtableSinkConfigBuilder getBuilder() {
    return BigtableSinkConfigBuilder.aBigtableSinkConfig()
      .setReferenceName(VALID_REF)
      .setTable(VALID_TABLE)
      .setInstance(VALID_INSTANCE)
      .setProject(VALID_PROJECT)
      .setServiceFilePath(VALID_ACCOUNT_FILE_PATH)
      .setKeyAlias(VALID_KEY_ALIAS)
      .setColumnMappings(VALID_COLUMN_MAPPING)
      .setBigtableOptions(VALID_BIGTABLE_OPTIONS);
  }

  private static void validateConfigValidationFail(BigtableSinkConfig config, String propertyValue) {
    try {
      config.validate(null);
      Assert.fail(String.format("Expected to throw %s", InvalidConfigPropertyException.class.getName()));
    } catch (InvalidConfigPropertyException e) {
      Assert.assertEquals(propertyValue, e.getProperty());
    }
  }
}
