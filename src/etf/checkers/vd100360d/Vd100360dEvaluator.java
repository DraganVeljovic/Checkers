package etf.checkers.vd100360d;

import static etf.checkers.CheckersConsts.BLK;
import static etf.checkers.CheckersConsts.BLK_KING;
import static etf.checkers.CheckersConsts.BLK_PAWN;
import static etf.checkers.CheckersConsts.H;
import static etf.checkers.CheckersConsts.RED;
import static etf.checkers.CheckersConsts.RED_KING;
import static etf.checkers.CheckersConsts.RED_PAWN;
import static etf.checkers.CheckersConsts.W;
import static etf.checkers.CheckersConsts.BLANK;
import etf.checkers.Evaluator;

public class Vd100360dEvaluator implements Evaluator {

	public int evaluateWithPlayerPerspective(int[] bs, int player) {
		int[] pawns = new int[2], kings = new int[2];

		for (int i = 0; i < H * W; i++) {
			int v = bs[i];
			switch (v) {
			case RED_PAWN:
			case BLK_PAWN:
				pawns[v % 4] += 1;
				break;
			case RED_KING:
			case BLK_KING:
				kings[v % 4] += 1;
				break;
			}
		}

		return ((player == RED) ? 1 : -1)
				* (1 * (pawns[RED] - pawns[BLK]) + 3 * (kings[RED] - kings[BLK]));
	}

	@Override
	public int evaluate(int[] bs) {
		int[] pawns = new int[2], 
				kings = new int[2], 
				almostKing = new int[2];//, 
				//positionValue = new int[2],
				//kingCenter = new int[2];
		
		// int redFiguresCount = 0, blackFiguresCount = 0;

		for (int i = 0; i < H * W; i++) {
			int v = bs[i];
			switch (v) {
			case RED_PAWN:
				// redFiguresCount++;
			case BLK_PAWN:
				// if (v == BLK_PAWN)
				// blackFiguresCount++;

				pawns[v % 4] += 1;

				almostKing[v % 4] += addAlmostKingCoefficient(bs, i);

				//positionValue[v % 4] += addSimplePositionValue(bs, i);
				
				//kingCenter[v % 4] += addKingCenterCoefficient(bs, i);

				break;
			case RED_KING:
				// redFiguresCount++;
			case BLK_KING:
				// blackFiguresCount++;

				kings[v % 4] += 1;

				//positionValue[v % 4] += addSimplePositionValue(bs, i);
				
				//kingCenter[v % 4] += addKingCenterCoefficient(bs, i);

				break;
			}
		}

		int[][] safeFiguresCount = countSafeFigures(bs);

		// int[] unoccupiedFieldsOnPromotionLine =
		// countUnoccupiedFieldsOnPromotionLine(bs);

		/*
		 * int firstPart = ((pawns[RED] + kings[RED] + pawns[BLK] + kings[BLK])
		 * < 8) ? (3 * (pawns[RED] - pawns[BLK]) + 5 * (kings[RED] -
		 * kings[BLK])) : almostKing[RED] - almostKing[BLK];
		 * 
		 * int secondPart = 3 (safeFiguresCount[0][RED] -
		 * safeFiguresCount[0][BLK]) + 5 (safeFiguresCount[1][RED] -
		 * safeFiguresCount[1][BLK]);
		 * 
		 * return createScore(firstPart, secondPart);
		 */
		/*
		return createScore(3 * (pawns[RED] - pawns[BLK]) + 5
				* (kings[RED] - kings[BLK]), almostKing[RED] - almostKing[BLK], 
				positionValue[RED] - positionValue[BLK], kingCenter[RED] - kingCenter[BLK],
				5 * (safeFiguresCount[0][RED] - safeFiguresCount[0][BLK]) + 0
						* (safeFiguresCount[1][RED] - safeFiguresCount[1][BLK]));
		*/
		
		return createScore((3 * (pawns[RED] - pawns[BLK]) + 5
				* (kings[RED] - kings[BLK])), (almostKing[RED] - almostKing[BLK]),
				5 * (safeFiguresCount[0][RED] - safeFiguresCount[0][BLK]));
		
		/*
		 * return ((pawns[RED] + kings[RED] + pawns[BLK] + kings[BLK]) < 8) ?
		 * ((3 * (pawns[RED] - pawns[BLK]) + 5 * (kings[RED] - kings[BLK])) * 4
		 * + (positionValue[RED] - positionValue[BLK]) / 10) : (almostKing[RED]
		 * - almostKing[BLK]) * 2 + (3 (safeFiguresCount[0][RED] -
		 * safeFiguresCount[0][BLK]) + 5 (safeFiguresCount[1][RED] -
		 * safeFiguresCount[1][BLK])) * 7 +
		 * (unoccupiedFieldsOnPromotionLine[RED] -
		 * unoccupiedFieldsOnPromotionLine[BLK]) * 3;
		 */

		/*
		 * return positionValue[RED] - positionValue[BLK] + (3 *
		 * (safeFiguresCount[0][RED] - safeFiguresCount[0][BLK]) + 5 *
		 * (safeFiguresCount[1][RED] - safeFiguresCount[1][BLK])) 7 +
		 * (unoccupiedFieldsOnPromotionLine[RED] -
		 * unoccupiedFieldsOnPromotionLine[BLK]) 3;
		 */
	}

	private int addAlmostKingCoefficient(int[] bs, int position) {

		int value = bs[position];

		if (value == RED_KING || value == BLK_KING || value == BLANK)
			return 0;

		for (int i = 0; i < W; i++) {
			if (position > i * W && position < W * (i + 1)) {
				if (value == RED_PAWN)
					if (i < (W / 2))
						return (W - i);
					else
						return 0;
				if (value == BLK_PAWN)
					if (i >= (W / 2))
						return i;
					else
						return 0;
			}
		}

		return 0;

	}

	private int addPositionValue(int[] bs, int position) {

		int positionValue = 0;

		int value = bs[position];

		if ((position >= 0 && position < W)
				|| (position >= (bs.length - W) && position < bs.length)) {

			if ((position >= 0 && position < W)) {

				if (value == RED_PAWN || value == RED_KING) {

					if (position == 0) {
						if (bs[position + W + 1] == BLK_KING
								|| bs[position + W + 1] == BLK_PAWN) {
							return 20;
						} else {
							return 10;
						}
					}

					if (position == W - 1) {
						if (bs[position + W - 1] == BLK_KING
								|| bs[position + W - 1] == BLK_PAWN) {
							return 20;
						} else {
							return 10;
						}
					}

					positionValue = 10;

					if (bs[position + W - 1] == BLK_PAWN
							|| bs[position + W - 1] == BLK_KING) {
						positionValue += 10;
					}

					if (bs[position + W + 1] == BLK_PAWN
							|| bs[position + W + 1] == BLK_KING) {
						positionValue += 10;
					}

					return positionValue;

				} else {
					return 10;
				}

			} else {

				if (value == BLK_PAWN || value == BLK_KING) {

					if (position == bs.length - W) {
						if (bs[position - W + 1] == RED_KING
								|| bs[position - W + 1] == RED_PAWN) {
							return 20;
						} else {
							return 10;
						}
					}

					if (position == bs.length - 1) {
						if (bs[position - W - 1] == RED_KING
								|| bs[position - W - 1] == RED_PAWN) {
							return 20;
						} else {
							return 10;
						}
					}

					positionValue = 10;

					if (bs[position - W - 1] == RED_PAWN
							|| bs[position - W - 1] == RED_KING) {
						positionValue += 10;
					}

					if (bs[position - W + 1] == RED_PAWN
							|| bs[position - W + 1] == RED_KING) {
						positionValue += 10;
					}

					return positionValue;

				} else {
					return 10;
				}

			}

			// from second to seventh row
		} else {

			if (value == RED_PAWN || value == RED_KING) {

				if ((position % W != 0) && (position % W != (W - 1))) {
					if ((bs[position - W - 1] == BLK_KING || bs[position - W
							- 1] == BLK_PAWN)) {

						if (bs[position + W + 1] == RED_PAWN
								|| bs[position + W + 1] == RED_KING) {
							positionValue += 10;
						} else {
							positionValue -= 10;
						}

					}

					if (bs[position - W + 1] == BLK_KING
							|| bs[position - W + 1] == BLK_PAWN) {

						if (bs[position + W - 1] == RED_PAWN
								|| bs[position + W - 1] == RED_KING) {
							positionValue += 10;
						} else {
							positionValue -= 10;
						}

					}

					if (bs[position + W - 1] == BLK_KING) {

						if (bs[position - W + 1] == RED_KING
								|| bs[position - W + 1] == RED_PAWN) {
							if (value == RED_KING) {
								positionValue += 15;
							} else {
								positionValue += 10;
							}
						} else {
							positionValue -= 10;
						}

					} else if (bs[position + W - 1] == RED_PAWN
							|| bs[position + W - 1] == RED_KING) {
						positionValue += 10;
					}

					if (bs[position + W + 1] == BLK_KING) {

						if (bs[position - W - 1] == RED_KING
								|| bs[position - W - 1] == RED_PAWN) {
							if (value == RED_KING) {
								positionValue += 15;
							} else {
								positionValue += 10;
							}
						} else {
							positionValue -= 10;
						}

					} else if (bs[position + W + 1] == RED_PAWN
							|| bs[position + W + 1] == RED_KING) {
						positionValue += 10;
					}

					return positionValue;

				} else {
					if (position % W == 0) {
						if (value == RED_PAWN) {
							if (bs[position + W + 1] == BLK_PAWN
									|| bs[position + W + 1] == BLK_KING) {
								return 20;
							} else {
								return 10;
							}
						}

						if (value == RED_KING) {
							if (bs[position + W + 1] == BLK_PAWN
									|| bs[position + W + 1] == BLK_KING) {
								if (bs[position - W + 1] == BLK_PAWN
										|| bs[position - W + 1] == BLK_KING) {
									return 30;
								} else {
									return 20;
								}
							} else {
								return 10;
							}
						}
					}

					if (position % W == (W - 1)) {
						if (value == RED_PAWN) {
							if (bs[position + W - 1] == BLK_PAWN
									|| bs[position + W - 1] == BLK_KING) {
								return 20;
							} else {
								return 10;
							}
						}

						if (value == RED_KING) {
							if (bs[position + W - 1] == BLK_PAWN
									|| bs[position + W - 1] == BLK_KING) {
								if (bs[position - W - 1] == BLK_PAWN
										|| bs[position - W - 1] == BLK_KING) {
									return 30;
								} else {
									return 20;
								}
							} else {
								return 10;
							}
						}
					}
				}

				return 0;
			}

			if (value == BLK_PAWN || value == BLK_KING) {

				if ((position % W != 0) && (position % W != (W - 1))) {
					if (bs[position + W - 1] == RED_KING
							|| bs[position + W - 1] == RED_PAWN) {

						if (bs[position - W + 1] == BLK_PAWN
								|| bs[position - W + 1] == BLK_KING) {
							positionValue += 10;
						} else {
							positionValue -= 10;
						}

					}

					if (bs[position + W + 1] == RED_KING
							|| bs[position + W + 1] == RED_PAWN) {

						if (bs[position - W - 1] == BLK_PAWN
								|| bs[position - W - 1] == BLK_KING) {
							positionValue += 10;
						} else {
							positionValue -= 10;
						}

					}

					if (bs[position - W - 1] == RED_KING) {

						if (bs[position + W + 1] == BLK_KING
								|| bs[position + W + 1] == BLK_PAWN) {
							if (value == BLK_KING) {
								positionValue += 15;
							} else {
								positionValue += 10;
							}
						} else {
							positionValue -= 10;
						}

					} else if (bs[position + W - 1] == BLK_PAWN
							|| bs[position + W - 1] == BLK_KING) {
						positionValue += 10;
					}

					if (bs[position - W + 1] == RED_KING) {

						if (bs[position + W - 1] == BLK_KING
								|| bs[position + W - 1] == BLK_PAWN) {
							if (value == BLK_KING) {
								positionValue += 15;
							} else {
								positionValue += 10;
							}
						} else {
							positionValue -= 10;
						}

					} else if (bs[position + W - 1] == BLK_PAWN
							|| bs[position + W - 1] == BLK_KING) {
						positionValue += 10;
					}

					return positionValue;

				} else {

					if (position % W == 0) {
						if (value == BLK_PAWN) {
							if (bs[position - W + 1] == RED_PAWN
									|| bs[position - W + 1] == RED_KING) {
								return 20;
							} else {
								return 10;
							}
						}

						if (value == BLK_KING) {
							if (bs[position - W + 1] == RED_PAWN
									|| bs[position - W + 1] == RED_KING) {
								if (bs[position + W + 1] == RED_PAWN
										|| bs[position + W + 1] == RED_KING) {
									return 30;
								} else {
									return 20;
								}
							} else {
								return 10;
							}
						}
					}

					if (position % W == (W - 1)) {
						if (value == BLK_PAWN) {
							if (bs[position - W - 1] == RED_PAWN
									|| bs[position - W - 1] == RED_KING) {
								return 20;
							} else {
								return 10;
							}
						}

						if (value == BLK_KING) {
							if (bs[position - W - 1] == RED_PAWN
									|| bs[position - W - 1] == RED_KING) {
								if (bs[position + W - 1] == RED_PAWN
										|| bs[position + W - 1] == RED_KING) {
									return 30;
								} else {
									return 20;
								}
							} else {
								return 10;
							}
						}
					}

				}

				return 0;
			}

			return 0;

		}

	}

	private int[][] countSafeFigures(int bs[]) {
		int[] safePawns = new int[2];
		int[] safeKings = new int[2];

		for (int i = 0; i < 2; i++) {
			safePawns[i] = 0;
			safeKings[i] = 0;
		}

		for (int i = 0; i < W; i++) {
			switch (bs[i]) {
			case RED_PAWN:
				safePawns[RED]++;
				break;
			case BLK_PAWN:
				safePawns[BLK]++;
				break;
			case RED_KING:
				safeKings[RED]++;
				break;
			case BLK_KING:
				safeKings[BLK]++;
				break;
			}

			switch (bs[bs.length - W + i]) {
			case RED_PAWN:
				safePawns[RED]++;
				break;
			case BLK_PAWN:
				safePawns[BLK]++;
				break;
			case RED_KING:
				safeKings[RED]++;
				break;
			case BLK_KING:
				safeKings[BLK]++;
				break;
			}

			if (i != 0 && i != (W - 1)) {
				switch (bs[i * 8]) {
				case RED_PAWN:
					safePawns[RED]++;
					break;
				case BLK_PAWN:
					safePawns[BLK]++;
					break;
				case RED_KING:
					safeKings[RED]++;
					break;
				case BLK_KING:
					safeKings[BLK]++;
					break;
				}

				switch (bs[i * 8 - 1]) {
				case RED_PAWN:
					safePawns[RED]++;
					break;
				case BLK_PAWN:
					safePawns[BLK]++;
					break;
				case RED_KING:
					safeKings[RED]++;
					break;
				case BLK_KING:
					safeKings[BLK]++;
					break;
				}

			}
		}

		int[][] returnMatrix = new int[2][];

		returnMatrix[0] = safePawns;
		returnMatrix[1] = safeKings;

		return returnMatrix;
	}

	private int[] countUnoccupiedFieldsOnPromotionLine(int[] bs) {

		int[] returnArray = new int[2];

		for (int i = 0; i < 2; i++) {
			returnArray[i] = 0;
		}

		for (int i = 0; i < W; i++) {

			if (bs[bs.length - W + i] == BLANK
					|| bs[bs.length - W + i] == RED_PAWN
					|| bs[bs.length - W + i] == RED_KING)
				returnArray[RED]++;

			if (bs[i] == BLANK || bs[i] == BLK_PAWN || bs[i] == BLK_KING)
				returnArray[BLK]++;
		}

		return returnArray;
	}
	
	private int addSimplePositionValue(int[] bs, int position) {
		if (position < W || position >= (bs.length - W) || (position % W == 0) || (position % W == (W - 1))) {
			return 0;
		} else {
			int positionValue = 0;
			
			if (bs[position] == RED_PAWN || bs[position] == RED_KING) {
				
				if (bs[position - W - 1] == BLK_PAWN || bs[position - W - 1] == BLK_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position - W - 1] == RED_PAWN || bs[position - W - 1] == RED_KING) {
					positionValue += 10; 
				}
				
				if (bs[position - W + 1] == BLK_PAWN || bs[position - W + 1] == BLK_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position - W + 1] == RED_PAWN || bs[position - W + 1] == RED_KING) {
					positionValue += 10; 
				}
				
				if (bs[position + W - 1] == BLK_PAWN || bs[position + W - 1] == BLK_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position + W - 1] == RED_PAWN || bs[position + W - 1] == RED_KING) {
					positionValue += 10; 
				}
				
				if (bs[position + W + 1] == BLK_PAWN || bs[position + W + 1] == BLK_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position + W + 1] == RED_PAWN || bs[position + W + 1] == RED_KING) {
					positionValue += 10; 
				}
			
			} else if (bs[position] == BLK) {
				
				if (bs[position - W - 1] == BLK_PAWN || bs[position - W - 1] == BLK_KING) {
					positionValue += 10; 
				}
				
				if (bs[position - W - 1] == RED_PAWN || bs[position - W - 1] == RED_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position - W + 1] == BLK_PAWN || bs[position - W + 1] == BLK_KING) {
					positionValue += 10; 
				}
				
				if (bs[position - W + 1] == RED_PAWN || bs[position - W + 1] == RED_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position + W - 1] == BLK_PAWN || bs[position + W - 1] == BLK_KING) {
					positionValue += 10; 
				}
				
				if (bs[position + W - 1] == RED_PAWN || bs[position + W - 1] == RED_KING) {
					positionValue -= 10; 
				}
				
				if (bs[position + W + 1] == BLK_PAWN || bs[position + W + 1] == BLK_KING) {
					positionValue += 10; 
				}
				
				if (bs[position + W + 1] == RED_PAWN || bs[position + W + 1] == RED_KING) {
					positionValue -= 10; 
				}
			
			}
			
			return positionValue;
		}
	}
	
	private int addKingCenterCoefficient(int[] bs, int position) {

		int value = bs[position];

		if (value == RED_PAWN || value == BLK_PAWN || value == BLANK)
			return 0;

		for (int i = 0; i < W; i++) {
			if (position > i * W && position < W * (i + 1)) {
				if (value == RED_KING)
					if (i > (W / 2) - 2 && i < W)
							return i;
						else
							return 0;
				if (value == BLK_KING)
					if (i > 0 && i < (W / 2) + 2)
						return W - i;
					else 
						return 0;
			}
		}

		return 0;

	}
	
	private int createScore(int... args) {

		int result = 0;

		for (int i = 0; i < args.length - 1; i++) {

			result = (result + (args[i] % 100)) * 100;

		}

		result += args[args.length - 1] % 100;

		return result;
	}
}
