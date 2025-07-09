/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.provider;

import edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.RandomLoadProfileFactory;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.profile.NbwTemperatureDependantLoadProfile;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Interface defining a provider for {@link LoadProfile}s and their {@link LoadProfileFactory}. */
public interface LoadProfileProvider {

  /** A list of all {@link LoadProfile}, that are known. */
  List<? extends LoadProfile> loadedProfiles =
      ServiceLoader.load(LoadProfileProvider.class).stream()
          .map(ServiceLoader.Provider::get)
          .map(LoadProfileProvider::getProfiles)
          .flatMap(Collection::stream)
          .toList();

  /** A map of all known {@link LoadProfile} to their {@link LoadProfileFactory}. */
  Map<? extends LoadProfile, ? extends LoadProfileFactory<?, ?>> profileToFactories =
      ServiceLoader.load(LoadProfileProvider.class).stream()
          .map(ServiceLoader.Provider::get)
          .map(LoadProfileProvider::getFactories)
          .flatMap(map -> map.entrySet().stream())
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

  /** Returns a list of {@link LoadProfile}s. */
  List<? extends LoadProfile> getProfiles();

  /** Returns a map of {@link LoadProfile} to {@link LoadProfileFactory}. */
  Map<LoadProfile, LoadProfileFactory<?, ?>> getFactories();

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
              LoadProfile.RandomLoadProfile.values(),
              LoadProfile.DefaultLoadProfiles.values())
          .flatMap(Arrays::stream)
          .toList();
    }

    @Override
    public Map<LoadProfile, LoadProfileFactory<?, ?>> getFactories() {
      BdewLoadProfileFactory bdewLoadProfileFactory = new BdewLoadProfileFactory();

      Map<LoadProfile, LoadProfileFactory<?, ?>> factories = new HashMap<>();

      Arrays.stream(BdewStandardLoadProfile.values())
          .forEach(profile -> factories.put(profile, bdewLoadProfileFactory));

      factories.put(
          LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE, new RandomLoadProfileFactory());

      return factories;
    }
  }
}
