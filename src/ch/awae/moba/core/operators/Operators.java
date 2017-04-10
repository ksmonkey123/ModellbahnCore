package ch.awae.moba.core.operators;

public class Operators {

	private final static IOperator[] list = { //
			new CenterBaseOperator(), //
			new CenterMacroOperator(), //
			new BottomOperator(), //
	};

	public static int getCount() {
		return list.length;
	}

}
