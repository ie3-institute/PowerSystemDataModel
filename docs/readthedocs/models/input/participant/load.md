(load-model)=

# Load

Model of (mainly) domestic loads.

## Attributes, Units and Remarks

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     -
     -

   * - id
     -
     - Human readable identifier

   * - operator
     -
     - [optional]

   * - operatesFrom/operatesUntil
     -
     - Timely restriction of operation [optional]

   * - node
     -
     -

   * - qCharacteristics
     -
     - [Reactive power characteristic](#participant-general-q-characteristic) to follow

   * - loadProfile
     -
     - [Power profile key](#load-pp) as model behaviour

   * - eConsAnnual
     - kWh
     - Annual energy consumption

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     -
     - Rated power factor

   * - controllingEm
     -
     - UUID reference to an [Energy Management Unit](#em_model) that is controlling
       this system participant. Field can be empty, if this participant
       is not controlled.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!

(load-pp)=

## Power Profile Keys

For a load input a {code}`PowerProfileKey` can be specified. This key is used to map the load input with a power profile
during a simulation. The key is normally provided as a {code}`String`. If this key matches the {code}`PowerProfileKey` of
one of the built-in load {code}`LoadProfile`, the corresponding load profile will be applied. If no profile should be used,
for example when using primary data via a time series, the field needs to be left empty.


## Power Profiles

The {code}`PowerProfile` is an interface that can be used to define power profiles. Each profile needs to have a {code}`PowerProfileKey`.
The data model uses the built-in profiles internally. The {code}`LoadProfile` is an interface that extends the {code}`PowerProfile`
interface.

To assist the user in marking the desired load profile, the enum {code}`BdewLoadProfile` provides a collection of
commonly known German standard electricity load profiles, defined by the bdew (Bundesverband der Energie- und
Wasserwirtschaft; engl. Federal Association of the Energy and Water Industry). For more details see
[the corresponding website (German only)](https://www.bdew.de/energie/standardlastprofile-strom/).

Furthermore there are {code}`TemperatureDependantLoadProfiles` which can be used to note usage of load profiles for night heating storages or heat pumps for example.
The profiles rely on the VDN description for interruptable loads.
For more details see [here (German only)](https://www.bdew.de/media/documents/LPuVe-Praxisleitfaden.pdf).
{code}`NbwTemperatureDependantLoadProfiles` provides sample temperature dependant load profiles that can be used.
The `NbwTemperatureDependantLoadProfiles` consists of load profiles "ep1" for heat pumps and "ez2" for night storage heating.
