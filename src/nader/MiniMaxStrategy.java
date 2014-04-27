package nader;

import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MiniMaxStrategy {

	// get the score for each legal move, return the move with the highest score
	public static Move getBestMove(StateMachine stateMachine,
			MachineState currentState, Role role)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

		List<Move> moves = stateMachine.getLegalMoves(currentState, role);

		int bestScoreSoFar = -1, score;
		Move bestMoveSoFar = null;

		for (Move move : moves) {
			score = getScore(stateMachine, role, currentState, move);
			if (score > bestScoreSoFar) {
				bestScoreSoFar = score;
				bestMoveSoFar = move;
				if (bestScoreSoFar == 100)
					break;
			}
		}
		return bestMoveSoFar;
	}

	// Get the minimax score for a move
	private static int getScore(StateMachine stateMachine, Role role, MachineState currentState, Move move)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {

		int worstScoreSoFar = 100;

	    /**
	     * Returns a list containing every joint move possible in the given state.
	     * A joint move consists of one move for each role, with the moves in the
	     * same ordering that their roles have in {@link #getRoles()}.
	     * <p>
	     * The list of possible joint moves is the Cartesian product of the lists
	     * of legal moves available for each player.
	     * <p>
	     * If only one player has more than one legal move, then the number of
	     * joint moves returned will equal the number of possible moves for that
	     * player.
	     */
		List<List<Move>> legalJoinMoves = stateMachine.getLegalJointMoves(currentState, role, move);

		for (List<Move> legalJoinMove : legalJoinMoves) {
			int bestScoreSoFar = -1;
			MachineState stateAfterMove = stateMachine.getNextState(currentState, legalJoinMove);
			if (stateMachine.isTerminal(stateAfterMove)) {
				bestScoreSoFar = stateMachine.getGoal(stateAfterMove, role);
			} else {
				// Choose the move for us in the next state which maximizes our score
				List<Move> moves = stateMachine.getLegalMoves(stateAfterMove, role);
				for (Move myNextMove : moves) {
					int bestScoreAfterMove = getScore(stateMachine, role, stateAfterMove, myNextMove);
					bestScoreSoFar = Math.max(bestScoreSoFar, bestScoreAfterMove);
					if (bestScoreSoFar == 100)
						break;
				}
			}

			// Choose the joint move for the opponents that minimizes
			// our score
			worstScoreSoFar = Math.min(worstScoreSoFar, bestScoreSoFar);
			if (worstScoreSoFar == 0)
				break;
		}
		return worstScoreSoFar;
	}
}
