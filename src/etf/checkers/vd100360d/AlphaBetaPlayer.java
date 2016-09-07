package etf.checkers.vd100360d;

import java.util.List;
import java.util.Stack;

import etf.checkers.CheckersPlayer;
import etf.checkers.Evaluator;
import etf.checkers.GradedCheckersPlayer;
import etf.checkers.Move;
import etf.checkers.SimpleEvaluator;
import etf.checkers.Utils;
import static etf.checkers.CheckersConsts.*;

/** This is a skeleton for an alpha beta checkers player. */
public class AlphaBetaPlayer extends CheckersPlayer implements
		GradedCheckersPlayer {

	/** Constant values to mark which player is on the move during calculations. */
	protected static final int MAX = 1;
	protected static final int MIN = -1;

	/** The number of pruned subtrees for the most recent deepening iteration. */
	protected int pruneCount;
	protected Evaluator sbe;

	/** Store score of last pruned node. */
	protected int lastPrunedNodeScore = Integer.MIN_VALUE;

	public AlphaBetaPlayer(String name, int side) {
		super(name, side);
		// Use SimpleEvaluator to score terminal nodes
		sbe = new SimpleEvaluator();
	}

	public void calculateMove(int[] bs) {
		/* Remember to stop expanding after reaching depthLimit */
		/* Also, remember to count the number of pruned subtrees. */

		int localDepthLimit = 1;
		
		while (localDepthLimit <= depthLimit) {

			Move move = alphaBetaPruning(bs, MAX, localDepthLimit, 0,
					Integer.MIN_VALUE, Integer.MAX_VALUE).getBestMove();

			if (move != null) {
				setMove(move);
			}
			
			if (Utils.verbose) {
				System.out.println("Prune count: " + pruneCount);
			
				System.out.println("Last pruned node score: " + lastPrunedNodeScore);
			}

			localDepthLimit++;
		}
		
		if (Utils.verbose) System.out.println("Best move is: " + getMove());

	}

	private Best alphaBetaPruning(int[] bs, int player, int depthLimit,
			int currentDepth, int alpha, int beta) {
	
		int bestScore;

		Move bestMove = null;
		
		int ab_side = (player == MAX) ? side : Utils.otherSide(side);

		List<Move> possibleMoves = Utils.getAllPossibleMoves(bs, ab_side);

		if (possibleMoves.size() == 0 || currentDepth == depthLimit) {
			bestScore = sbe.evaluate(bs);

			if (side == BLK) {
				bestScore = -bestScore;
			}

			return new Best(bestMove, bestScore);
		}

		if (player == MAX) {
			bestScore = Integer.MIN_VALUE;
		} else {
			bestScore = Integer.MAX_VALUE;
		}

		int numberOfChildren = possibleMoves.size();

		for (Move move : possibleMoves) {

			numberOfChildren--;

			Stack<Integer> rv = Utils.execute(bs, move);

			// int[] nextState = getNextState(bs, move);

			Best currentBest = alphaBetaPruning(bs, -player,
					depthLimit, currentDepth + 1, alpha, beta);

			// int currentScore = alphaBetaPruning(bs, -player, depthLimit,
			// currentDepth + 1, alpha, beta);

			if (player == MAX)
				if (currentBest.getBestScore() > bestScore) {

					bestScore = currentBest.getBestScore();

					bestMove = move;

					if (bestScore >= beta) {

						Utils.revert(bs, rv);

						pruneCount += numberOfChildren;

						lastPrunedNodeScore = bestScore;

						return new Best(bestMove, bestScore);
					}

					alpha = Math.max(alpha, bestScore);
					
				}
			
			if (player == MIN) 
				if (currentBest.getBestScore() < bestScore) {

					bestScore = currentBest.getBestScore();

					bestMove = move;

					if (bestScore <= alpha) {

						Utils.revert(bs, rv);

						pruneCount += numberOfChildren;

						lastPrunedNodeScore = bestScore;

						return new Best(bestMove, bestScore);
					}

					beta = Math.min(beta, bestScore);
				}

			Utils.revert(bs, rv);
		}

		return new Best(bestMove, bestScore);
	}

	private int[] getNextState(int[] currentState, Move move) {
		int[] nextState = currentState.clone();

		Utils.execute(nextState, move);

		return nextState;
	}

	public int getPruneCount() {
		return pruneCount;
	}

	@Override
	public int getLastPrunedNodeScore() {
		return lastPrunedNodeScore;
	}
}
