package etf.checkers.vd100360d;

import etf.checkers.Move;

public class Best {
	
	private Move bestMove;
	
	private int bestScore;

	public Best(Move bestMove, int bestScore) {
		super();
		this.bestMove = bestMove;
		this.bestScore = bestScore;
	}

	public Move getBestMove() {
		return bestMove;
	}

	public void setBestMove(Move bestMove) {
		this.bestMove = bestMove;
	}

	public int getBestScore() {
		return bestScore;
	}

	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}
	
	
}
