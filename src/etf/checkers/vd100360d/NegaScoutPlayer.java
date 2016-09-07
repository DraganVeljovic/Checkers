package etf.checkers.vd100360d;

import static etf.checkers.CheckersConsts.BLK;
import static etf.checkers.CheckersConsts.RED;

import java.util.List;
import java.util.Stack;

import etf.checkers.CheckersPlayer;
import etf.checkers.Evaluator;
import etf.checkers.GradedCheckersPlayer;
import etf.checkers.Move;
import etf.checkers.SimpleEvaluator;
import etf.checkers.Utils;

public class NegaScoutPlayer extends CheckersPlayer implements
		GradedCheckersPlayer {

	/** The number of pruned subtrees for the most recent deepening iteration. */
	protected int pruneCount;
	protected Evaluator sbe;

	/** Store score of last pruned node. */
	protected int lastPrunedNodeScore = Integer.MIN_VALUE;

	public NegaScoutPlayer(String name, int side) {
		super(name, side);
		// Use SimpleEvaluator to score terminal nodes
		sbe = new Vd100360dEvaluator();
	}

	@Override
	public void calculateMove(int[] bs) {
		/* Remember to stop expanding after reaching depthLimit */
		/* Also, remember to count the number of pruned subtrees. */

		int localDepthLimit = 1;

		while (localDepthLimit <= depthLimit) {

			Move move = negaScouting(bs, side, localDepthLimit, 0, Integer.MIN_VALUE,
					Integer.MAX_VALUE).getBestMove();

			if (move != null)
				setMove(move);

			localDepthLimit++;
		}

	}

	private Best negaScouting(int[] bs, int player,int depthLimit, int currentDepth,
			int alpha, int beta) {

		int bestScore = Integer.MIN_VALUE;

		Move bestMove = null;
		
		int adaptiveBeta = beta;

		List<Move> possibleMoves = Utils.getAllPossibleMoves(bs, player);

		if (possibleMoves.size() == 0 || currentDepth == depthLimit) {
			bestScore = ((Vd100360dEvaluator)sbe).evaluateWithPlayerPerspective(bs, player);
			
			return new Best(bestMove, bestScore);
		}

		int numberOfChildren = possibleMoves.size();

		for (Move move : possibleMoves) {

			numberOfChildren--;

			Stack<Integer> rv = Utils.execute(bs, move);

			Best currentBest = negamax(bs, player, depthLimit, currentDepth + 1,
					-adaptiveBeta, -Math.max(alpha, bestScore));

			currentBest.setBestScore(-currentBest.getBestScore());

			if (currentBest.getBestScore() > bestScore) {
				
				if (adaptiveBeta == beta || currentDepth >= depthLimit-2) { 

					bestScore = currentBest.getBestScore();

					bestMove = move;
					
				} else {
					
					Best negativeBest = negaScouting(bs, Utils.otherSide(player), depthLimit, currentDepth + 1, -beta, -currentBest.getBestScore());
					
					bestMove = negativeBest.getBestMove();
					
					bestScore = -negativeBest.getBestScore();
				}
			}

			

			if (bestScore >= beta) {

				Utils.revert(bs, rv);

				pruneCount += numberOfChildren;

				lastPrunedNodeScore = bestScore;

				return new Best(bestMove, bestScore);
			}
			
			adaptiveBeta = Math.max(alpha, bestScore) + 1;

			Utils.revert(bs, rv);
		}

		return new Best(bestMove, bestScore);
	}

	
	private Best negamax(int[] bs, int player, int depthLimit, int currentDepth,
			int alpha, int beta) {

		int bestScore = Integer.MIN_VALUE;

		Move bestMove = null;

		List<Move> possibleMoves = Utils.getAllPossibleMoves(bs, player);

		if (possibleMoves.size() == 0 || currentDepth == depthLimit) {
			bestScore = ((Vd100360dEvaluator)sbe).evaluateWithPlayerPerspective(bs, player);
			
			return new Best(bestMove, bestScore);
		}

		int numberOfChildren = possibleMoves.size();

		for (Move move : possibleMoves) {

			numberOfChildren--;

			Stack<Integer> rv = Utils.execute(bs, move);

			Best currentBest = negamax(bs, Utils.otherSide(player), depthLimit, currentDepth + 1,
					-beta, -Math.max(alpha, bestScore));

			currentBest.setBestScore(-currentBest.getBestScore());

			if (currentBest.getBestScore() > bestScore) {

				bestScore = currentBest.getBestScore();

				bestMove = move;

			}

			if (bestScore >= beta) {

				Utils.revert(bs, rv);

				pruneCount += numberOfChildren;

				lastPrunedNodeScore = bestScore;

				return new Best(bestMove, bestScore);
			}

			Utils.revert(bs, rv);
		}

		return new Best(bestMove, bestScore);
	}

	
	@Override
	public int getPruneCount() {
		return pruneCount;
	}

	@Override
	public int getLastPrunedNodeScore() {
		return lastPrunedNodeScore;
	}

}
