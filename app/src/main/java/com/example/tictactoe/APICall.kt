package com.example.tictactoe

import retrofit2.http.*

interface APICall {
    // @Headers("application/json")
    @FormUrlEncoded
    @POST("/play")
    suspend fun addPlayer(
        @Field("playerId") playerId : String
    ):PlayResponse


    @FormUrlEncoded
    @POST("/submitMove")
    suspend fun submitMove(
        @Field("player") player : String,
        @Field("matchId") matchId : String,
        @Field("row") row : Int,
        @Field("column") column : Int
    ):SubmitMoveResponse

}