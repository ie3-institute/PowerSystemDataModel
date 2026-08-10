/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.input.connector.CableDeploymentInput;
import java.util.UUID;

/**
 * Associates parsed cable deployment data with the UUID of its line.
 *
 * @param lineUuid UUID of the line that owns the deployment data
 * @param deployment Parsed cable deployment data
 */
public record CableDeploymentInputData(UUID lineUuid, CableDeploymentInput deployment) {}
