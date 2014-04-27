package nader;

import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class CompulsiveStrategy {

	public static Move getBestMove(StateMachine stateMachine, MachineState currentState, Role role) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		int currentScore = 0, currentIndex = 0;
		List<Move> moves = stateMachine.getLegalMoves(currentState, role);

		for (int i = 0; i < moves.size(); i++) {
			MachineState nextState = stateMachine.getNextState(currentState, stateMachine.getRandomJointMove(currentState,role, moves.get(i)));
			int score = getMaxScore(stateMachine, nextState, role);
			if (score > currentScore) {
				currentScore = score;
				currentIndex = i;
			}
		}

		return moves.get(currentIndex);
	}

	// For each legal move, find out which one has the maximum utility.
	public static int getMaxScore(StateMachine stateMachine, MachineState state, Role role) throws MoveDefinitionException, GoalDefinitionException, TransitionDefinitionException {
		int currentScore = 0;

		if (stateMachine.isTerminal(state)) {
			return stateMachine.getGoal(state, role);
		}

		List<Move> moves = stateMachine.getLegalMoves(state, role);
		for (Move move : moves) {
			MachineState nextState = stateMachine.getNextState(state, stateMachine.getRandomJointMove(state, role, move));
			int score = getMaxScore(stateMachine, nextState, role);
			currentScore = Math.max(currentScore, score);
		}

		return currentScore;
	}
}
