package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import com.webcheckers.model.Move;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import spark.*;


import java.util.logging.Logger;

// File created by Beck Anderson
// Edited by Dhruv

public class PostValidateMoveRoute implements Route {

    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());
    private Game game;
    private Move move;
    private GameCenter gameCenter;
    private Gson gson;
    private Message msg;

    //private Piece piece;

    /**
     * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP
     * requests.
     *
     */
    public PostValidateMoveRoute(GameCenter gameCenter, Gson gson) {
        this.gameCenter = gameCenter;
        this.gson = gson;
        LOG.config("PostValidateRoute is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.finer("PostValidateRoute is invoked");
        //String gameID = request.queryParams(GetGameRoute.Game_ID);
        String actionData = request.queryParams("actionData");

        move = gson.fromJson(actionData, Move.class);
        gameCenter.storeMove(move);
        String userName = request.session().attribute("userName");
        game = gameCenter.getGame(userName);

        if ( game.getActiveColor() == Game.ActiveColor.WHITE ){
            move = move.invertMove();
            msg = Message.info("Oppenent will play first"); // problem from here on
        }
        Player activePLayer = gameCenter.getGame(userName).getActivePlayer();
        if(gameCenter.getGame(userName).getRed()==activePLayer){
            if(gameCenter.getGame(userName).getGameBoardRed().validMove(move)){
                msg = Message.info("valid Move");
            }else{
                msg = Message.error("invalid move, please try another.");
            }
        }else {
            if(gameCenter.getGame(userName).getGameBoardWhite().validMove(move.invertMove())){
                msg = Message.info("valid Move");
            }else {
                msg = Message.error("invalid move, please try another.");
            }
        }
        String json;
        json = gson.toJson(msg);
        return json;
    }
}