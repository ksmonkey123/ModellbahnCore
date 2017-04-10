package ch.awae.moba.core.model;

import static ch.awae.moba.core.model.Sector.CENTER;
import static ch.awae.moba.core.model.Sector.LEFT;
import static ch.awae.moba.core.model.Sector.BOTTOM;
import static ch.awae.moba.core.model.Sector.RIGHT;

public enum Path {
	// ######### BOTTOM PATHS #########
	B_01_L(BOTTOM, 1, 0x040101, "bottom.01"),
	B_02_L(BOTTOM, 1, 0x040202, "bottom.02"),
	B_03_L(BOTTOM, 1, 0x040403, "bottom.03"),
	B_04_L(BOTTOM, 1, 0x040804, "bottom.04"),
	B_05_L(BOTTOM, 1, 0x041005, "bottom.05"),
	B_06_L(BOTTOM, 1, 0x042006, "bottom.06"),
	B_07_L(BOTTOM, 1, 0x044007, "bottom.07"),
	B_08_L(BOTTOM, 1, 0x048008, "bottom.08"),
	B_09_L(BOTTOM, 1, 0x050009, "bottom.09"),
	B_10_L(BOTTOM, 1, 0x06000a, "bottom.10"),
	B_02_R(BOTTOM, 1, 0x18020c, "bottom.r2"),
	B_03_R(BOTTOM, 1, 0x18040d, "bottom.r3"),
	B_04_R(BOTTOM, 1, 0x18080e, "bottom.r4"),
	B_05_R(BOTTOM, 1, 0x18100f, "bottom.r5"),
	B_CLEAR(BOTTOM, 1, 0x000000, "bottom.clear"),
	// ######### CENTER PATHS #########
	C_S_1_A(CENTER, 0x01, 0x0101, "center.1a"),
	C_S_1_B(CENTER, 0x01, 0x0202, "center.1b"),
	C_S_2_A(CENTER, 0x02, 0x0404, "center.2a"),
	C_S_2_B(CENTER, 0x02, 0x0808, "center.2b"),
	C_S_3_A(CENTER, 0x04, 0x1010, "center.3a"),
	C_S_3_B(CENTER, 0x04, 0x2020, "center.3b"),
	C_S_4_A(CENTER, 0x08, 0x4040, "center.4a"),
	C_S_4_B(CENTER, 0x08, 0x8080, "center.4b"),
	C_CLEAR(CENTER, 0x0f, 0x0000, "center.clear"),
	// ######### LEFS ############
	L_CLEAR(LEFT, 0x1f, 0x000000, "left.clear"),
	L_A_1_R(LEFT, 0x01, 0x030320, "left.A1_R"),
	L_B_1_R(LEFT, 0x07, 0x0c0308, "left.B1_R"),
	L_B_2_R(LEFT, 0x06, 0x0c0c10, "left.B2_R"),
	L_C_1_R(LEFT, 0x1f, 0x300301, "left.C1_R"),
	L_C_2_R(LEFT, 0x1e, 0x300c02, "left.C2_R"),
	L_C_3_R(LEFT, 0x18, 0x303003, "left.C3_R"),
	L_C_4_R(LEFT, 0x10, 0x30c004, "left.C4_R"),
	L_C_1_I(LEFT, 0x1f, 0x200141, "left.C1_I"),
	L_C_2_I(LEFT, 0x1e, 0x200442, "left.C2_I"),
	L_C_3_I(LEFT, 0x18, 0x201043, "left.C3_I"),
	L_C_4_I(LEFT, 0x10, 0x204044, "left.C4_I"),
	L_C_1_O(LEFT, 0x1f, 0x100281, "left.C1_O"),
	L_C_2_O(LEFT, 0x1e, 0x100882, "left.C2_O"),
	L_C_3_O(LEFT, 0x18, 0x102083, "left.C3_O"),
	L_C_4_O(LEFT, 0x10, 0x108084, "left.C4_O"),
	// ######### RIGHT PATHS ##############
	R_CLEAR(RIGHT, 0x3f, 0x000000, "right.clear"),
	R_A_1_R(RIGHT, 0x19, 0x030301, "right.A1_R"),
	R_A_2_R(RIGHT, 0x19, 0x030c02, "right.A2_R"),
	R_A_3_R(RIGHT, 0x3f, 0x033007, "right.A3_R"),
	R_A_4_R(RIGHT, 0x3f, 0x03c00b, "right.A4_R"),
	R_A_1_I(RIGHT, 0x19, 0x020111, "right.A1_I"),
	R_A_2_I(RIGHT, 0x19, 0x020412, "right.A2_I"),
	R_A_3_I(RIGHT, 0x3f, 0x021057, "right.A3_I"),
	R_A_4_I(RIGHT, 0x3f, 0x02405b, "right.A4_I"),
	R_A_1_O(RIGHT, 0x19, 0x010221, "right.A1_O"),
	R_A_2_O(RIGHT, 0x19, 0x010822, "right.A2_O"),
	R_A_3_O(RIGHT, 0x3f, 0x0120a7, "right.A3_O"),
	R_A_4_O(RIGHT, 0x3f, 0x0180ab, "right.A4_O"),
	R_B_1_R(RIGHT, 0x26, 0x0c030d, "right.B1_R"),
	R_B_2_R(RIGHT, 0x26, 0x0c0c0e, "right.B2_R"),
	R_B_3_R(RIGHT, 0x3f, 0x0c3004, "right.B3_R"),
	R_B_4_R(RIGHT, 0x3f, 0x0cc008, "right.B4_R"),
	R_B_1_I(RIGHT, 0x26, 0x08015d, "right.B1_I"),
	R_B_2_I(RIGHT, 0x26, 0x08045e, "right.B2_I"),
	R_B_3_I(RIGHT, 0x3f, 0x081044, "right.B3_I"),
	R_B_4_I(RIGHT, 0x3f, 0x084048, "right.B4_I"),
	R_B_1_O(RIGHT, 0x26, 0x0402ad, "right.B1_O"),
	R_B_2_O(RIGHT, 0x26, 0x0408ae, "right.B2_O"),
	R_B_3_O(RIGHT, 0x3f, 0x042084, "right.B3_O"),
	R_B_4_O(RIGHT, 0x3f, 0x048088, "right.B4_O"),
	// ######## ENUM INTERNALS ########
	;
	public final String title;
	public final Sector sector;
	public final int mask;
	public final int data;

	Path(Sector sector, int mask, int data, String title) {
		this.title = title;
		this.mask = mask;
		this.data = data;
		this.sector = sector;
	}

	public boolean collides(Path other) {
		return (this.sector == other.sector) && ((this.mask & other.mask) != 0);
	}
}
