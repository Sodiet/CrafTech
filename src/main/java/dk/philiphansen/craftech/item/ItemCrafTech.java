/*
 * Copyright (C) 2014 Philip Hansen and CrafTech contributors.
 *
 * This file is part of CrafTech.
 *
 * CrafTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CrafTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CrafTech.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.philiphansen.craftech.item;

import dk.philiphansen.craftech.CrafTech;
import net.minecraft.item.Item;

/**
 * Abstract class all trivial CrafTech items should be built on.
 * Handles adding the item to the CrafTech creative tab.
 */
abstract class ItemCrafTech extends Item {

	ItemCrafTech() {
		super();
		setCreativeTab(CrafTech.tabCrafTech);
	}

}
