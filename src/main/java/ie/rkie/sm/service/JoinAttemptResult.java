package ie.rkie.sm.service;

/**
 * Encapsulates the possible outcomes when attempting to join a game.
 */
public enum JoinAttemptResult {
	SUCCESS,
	ALREADY_JOINED,
	PLAYER_LIMIT_REACHED, 
	GAME_NOT_FOUND
}
