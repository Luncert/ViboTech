/*
 * This file is part of pnc-repressurized API.
 *
 *     pnc-repressurized API is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with pnc-repressurized API.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.luncert.vibotech.compat.pneumatic;

public interface IAirHandler {

  /**
   * Get the current pressure for this handler.
   *
   * @return the current pressure
   */
  float getPressure();

  /**
   * Returns the amount of air in this handler.  Note: amount of air = pressure * volume.
   *
   * @return the air in this air handler
   */
  int getAir();

  /**
   * Adds air to this handler.
   *
   * @param amount amount of air to add in mL, may be negative.
   */
  void addAir(int amount);

  /**
   * Gets the base volume of this handler.
   * When the volume decreases, the pressure will remain the same, meaning air will be lost.
   * When the volume increases, the air remains the same, meaning the pressure will drop.
   *
   * @return the base volume
   */
  int getBaseVolume();

  /**
   * Set the base volume of this air handler. May be useful if the base volume depends on factors other than the
   * number of volume upgrades.
   *
   * @param newBaseVolume the new base volume
   */
  void setBaseVolume(int newBaseVolume);

  /**
   * Get the effective volume of this air handler.
   * @return the effective volume, in mL
   */
  int getVolume();

  /**
   * Get the maximum pressure this handler can take.  Behaviour when more air is added is implementation-dependent
   * (e.g. items tend to stop accepting air, while blocks/machine tend to explode)
   *
   * @return the maximum pressure for this handler
   */
  float getMaxPressure();
}
