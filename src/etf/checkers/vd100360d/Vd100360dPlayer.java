package etf.checkers.vd100360d;

public class Vd100360dPlayer extends AlphaBetaPlayer {

	public Vd100360dPlayer(String name, int side) {
		super(name, side);
	
		sbe = new Vd100360dEvaluator();
	}

}
