package ie.rkie.sm.service;

/**
 * Encapsulates the possible outcomes when attempting to join a game.
 */
public enum JoinAttemptResult {
	SUCCESS("join.success"),
	ALREADY_JOINED("join.player.already.joined"),
	PLAYER_LIMIT_REACHED("join.player.limit.reached"),
	GAME_NOT_FOUND("join.token.not.found");

	private String message;

	private JoinAttemptResult(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
