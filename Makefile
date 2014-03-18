files = LogicBomb.java LogicBombBoard.java MineCell.java

all: LogicBomb run

LogicBomb: $(files)
	javac $(files)

run: LogicBomb
	java LogicBomb

