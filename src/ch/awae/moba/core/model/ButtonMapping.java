package ch.awae.moba.core.model;

import static ch.awae.moba.core.model.Sector.BOTTOM;
import static ch.awae.moba.core.model.Sector.CENTER;
import static ch.awae.moba.core.model.Sector.LEFT;
import static ch.awae.moba.core.model.Sector.RIGHT;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.logic.Logic;

public enum ButtonMapping implements Logic {
	// ###### BOTTOM BUTTONS ######
	B_TR_01("bottom.t01", BOTTOM, 0),
	B_TR_02("bottom.t02", BOTTOM, 1),
	B_TR_03("bottom.t03", BOTTOM, 2),
	B_TR_04("bottom.t04", BOTTOM, 3),
	B_TR_05("bottom.t05", BOTTOM, 4),
	B_TR_06("bottom.t06", BOTTOM, 5),
	B_TR_07("bottom.t07", BOTTOM, 6),
	B_TR_08("bottom.t08", BOTTOM, 7),
	B_TR_09("bottom.t09", BOTTOM, 8),
	B_TR_10("bottom.t10", BOTTOM, 9),
	B_ENT_L("bottom.enL", BOTTOM, 10),
	B_ENT_R("bottom.enR", BOTTOM, 11),
	B_CLEAR("bottom.clear", BOTTOM, 15),
	// ###### CENTER BUTTONS ######
	C_S_1_A("center.1a", CENTER, 0),
	C_S_1_B("center.1b", CENTER, 1),
	C_S_2_A("center.2a", CENTER, 2),
	C_S_2_B("center.2b", CENTER, 3),
	C_S_3_A("center.3a", CENTER, 4),
	C_S_3_B("center.3b", CENTER, 5),
	C_S_4_A("center.4a", CENTER, 6),
	C_S_4_B("center.4b", CENTER, 7),
	C_CLEAR("center.clear", CENTER, 8),
	// ###### RIGHT BUTTONS ##########
	R_TRK_1("right.t01", RIGHT, 0),
	R_TRK_2("right.t02", RIGHT, 1),
	R_TRK_3("right.t03", RIGHT, 2),
	R_TRK_4("right.t04", RIGHT, 3),
	R_PTH_A("right.p_A", RIGHT, 4),
	R_PTH_B("right.p_B", RIGHT, 5),
	R_CLR_A("right.clear_A", RIGHT, 6),
	R_CLR_B("right.clear_B", RIGHT, 7),
	// ###### LEFT BUTTONS #########
	L_TRK_1("left.t01", LEFT, 8), // relocated from T0 due to hardware issue
	L_TRK_2("left.t02", LEFT, 1),
	L_TRK_3("left.t03", LEFT, 2),
	L_TRK_4("left.t04", LEFT, 3),
	L_PTH_A("left.p_A", LEFT, 4),
	L_PTH_B("left.p_B", LEFT, 5),
	L_PTH_C("left.p_C", LEFT, 6),
	L_CLEAR("left.clear", LEFT, 7),
	// ###### GROUPINGS ######
	;

	public static final ButtonMapping[] BOTTOM_TRACKS = { B_TR_01, B_TR_02, B_TR_03, B_TR_04, B_TR_05, B_TR_06, B_TR_07,
			B_TR_08, B_TR_09, B_TR_10 };

	// ###### INTERNALS #######
	public final String title;
	public final Sector sector;
	public final int index;

	ButtonMapping(String title, Sector sector, int index) {
		this.title = title;
		this.sector = sector;
		this.index = index;
	}

	public static @Nullable ButtonMapping byTitle(String title) {
		for (ButtonMapping bm : values())
			if (bm.title.equals(title))
				return bm;
		return null;
	}

	@Override
	public boolean evaluate(@NonNull Model m) {
		return m.buttons.getState(this);
	}
}
