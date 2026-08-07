package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class UniqueInputEntity extends UniqueEntity implements InputEntity {
  private final Map<String, String> additionalInformation;

  public UniqueInputEntity(UUID uuid) {
    super(uuid);
    this.additionalInformation = new HashMap<>();
  }

  public Map<String, String> getAdditionalInformation() {
    return Collections.unmodifiableMap(additionalInformation);
  }

  protected void setAdditionalInformation(Map<String, String> additionalInformation) {
    if (additionalInformation == null) {
      return;
    }
    this.additionalInformation.putAll(additionalInformation);
  }

  @Override
  public abstract UniqueInputEntityCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UniqueInputEntity that)) {
      return false;
    }
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "UniqueInputEntity{"
        + "uuid=" + getUuid()
        + '}';
  }

  public abstract static class UniqueInputEntityCopyBuilder<B extends UniqueInputEntityCopyBuilder<B>> extends UniqueEntity.UniqueEntityCopyBuilder<B> {
    private Map<String, String> additionalInformation;

    protected UniqueInputEntityCopyBuilder(UniqueInputEntity entity) {
      super(entity);
      this.additionalInformation = new HashMap<>(entity.getAdditionalInformation());
    }

    public B additionalInformation(Map<String, String> additionalInformation) {
      this.additionalInformation.putAll(additionalInformation);
      return thisInstance();
    }

    protected Map<String, String> getAdditionalInformation() {
      return additionalInformation;
    }

    @Override
    public abstract UniqueInputEntity build();

    @Override
    protected abstract B thisInstance();
  }
}
