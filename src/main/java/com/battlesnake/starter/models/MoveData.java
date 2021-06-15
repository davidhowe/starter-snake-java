package com.battlesnake.starter.models;

public class MoveData {
    public int turn;
    public BoardData board;
    public GameData game;
    public SnakeData you;

    class GameData {
        public String id;
        public int timeout;
    }

    class BoardData {
        public int height;
        public int width;
        public SnakeData[] snakes;
        public XY[] food;
    }
    class XY {
        public int x;
        public int y;
    }
    class SnakeData {
        public String name;
        public int health;
        public XY[] body;
        public XY head;
        public int length;
    }
}

