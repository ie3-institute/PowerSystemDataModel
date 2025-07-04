/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** Interface defining a provider for {@link LoadProfile}s. */
public interface LoadProfileProvider {

  /** Returns a list of {@link LoadProfile}s. */
  List<? extends LoadProfile> getProfiles();

  /**
   * Default load profile provider. This class is used to provide the {@link LoadProfile}s defined
   * in the PSDM.
   */
  final class DefaultLoadProfileProvider implements LoadProfileProvider {

    @Override
    public List<? extends LoadProfile> getProfiles() {
      return Stream.of(
              BdewStandardLoadProfile.values(),
              NbwTemperatureDependantLoadProfile.values(),
              (LoadProfile[]) LoadProfile.RandomLoadProfile.values())
          .flatMap(Arrays::stream)
          .toList();
    }
  }
}
