import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;

import javax.swing.event.*;
import java.util.*;

//Please use the README text file for the game manual.
public class GameOfLife extends JFrame {

	//Parameters
	protected int wid = 20;
	protected int hei = 20;
	protected boolean[][] currMove = new boolean[hei][wid]; 
	protected boolean[][] newMove = new boolean[hei][wid]; 
	protected boolean Start = false;
	protected int numOfSteps;
	protected int Speed = 1000;
	protected Image BeforeOut;
	protected Graphics Grid;
	protected boolean FreeMode = false;
	protected Color backCol = Color.GRAY; 
	protected Color cellCol = Color.GREEN;
	protected int PercentageOfLive = 50;
	protected boolean wrapped = false;

	//Graphics components
	protected JPanel GridPanel = new JPanel();
	protected JPanel ControlPanel = new JPanel();
	protected JPanel ButtonPanel = new JPanel();
	protected JPanel InputPanel = new JPanel();
	protected JPanel ColorPanel = new JPanel();
	protected JSplitPane split = new JSplitPane();
	protected Timer timer; 
	protected JButton StartButton = new JButton();
	protected JButton ResetButton = new JButton();
	protected JButton RandomizeButton = new JButton();
	protected JLabel ShowSteps = new JLabel();
	protected JSlider SetSpeed = new JSlider(JSlider.HORIZONTAL, 1, 15, 1);
	protected JSlider SetDensity = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
	protected JTextField HeightIn = new JTextField();
	protected JTextField WidthIn = new JTextField();
	protected JButton SetFree = new JButton();
	protected JButton SetWrap = new JButton();
	
	//.....................CONSTRUCTOR.....................//
	public GameOfLife() {
		super();
		setTitle("Game Of Life");
		setSize(700, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
	/* Frame: ControlPanel, GridPanel, SplitPane
	 * ControlPanel: ButtonPanel, InputPanel, ColorPanel
	 */
	//..........CONTROLPANEL..........//
		getContentPane().add(ControlPanel);
		ControlPanel.setLayout(new GridLayout(0, 3));
		
		ControlPanel.add(ButtonPanel);
		ButtonPanel.setLayout(new BoxLayout(ButtonPanel, BoxLayout.Y_AXIS));
		
		ControlPanel.add(InputPanel);
		InputPanel.setLayout(new BoxLayout(InputPanel, BoxLayout.PAGE_AXIS));
		
		ControlPanel.add(ColorPanel);
		ColorPanel.setLayout(new GridLayout(0, 3));
		
	//BUTTON PANEL (Including Start/ Pause, Reset, Randomize, Custom Mode, Enable Wrap buttons, and number of iterations)
		ButtonPanel.add(StartButton);
		ButtonPanel.add(ResetButton);
		ButtonPanel.add(RandomizeButton);
		ButtonPanel.add(SetFree);
		ButtonPanel.add(SetWrap);
		ButtonPanel.add(ShowSteps);
		StartButton.setAlignmentX(CENTER_ALIGNMENT);
		ResetButton.setAlignmentX(CENTER_ALIGNMENT);
		RandomizeButton.setAlignmentX(CENTER_ALIGNMENT);
		SetFree.setAlignmentX(CENTER_ALIGNMENT);
		SetWrap.setAlignmentX(CENTER_ALIGNMENT);
		ShowSteps.setAlignmentX(CENTER_ALIGNMENT);
		
		//Configure Start Button
		StartButton.setText("Start");
		StartButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Start = !Start;
				runGame();
				SwitchStartStop();
			}
		});
		
		//Configure Reset Button
		ResetButton.setText("Reset");
		ResetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ResetBoard();
				ToPaint();
				numOfSteps = 0;
				ShowSteps.setText("Iteration: " + numOfSteps);
			}
		});
		
		//Configure Randomize Button
		RandomizeButton.setText("Randomize");
		RandomizeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				RandomizeBoard();
				ToPaint();
				numOfSteps = 0;
				ShowSteps.setText("Iteration: " + numOfSteps);
			}
		});
		
		//Configure Switch Mode Button
		SetFree.setText("Enter Custom Mode");
		SetFree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FreeMode = !FreeMode;
				SwitchFree();
			}
		});
		
		//Configure Wrap Button
		SetWrap.setText("Enable Wrap Mode");
		SetWrap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				wrapped = !wrapped;
				if (wrapped) {SetWrap.setText("Disable Wrap Mode");}
				else SetWrap.setText("Enable Wrap Mode");
			}
		});
		
		//Configure and show Steps
		ShowSteps.setText("Iteration: " + numOfSteps);
		
	//INPUT PANEL (Including Height, Width, Speed, and Density input)	
		JPanel HeightPanel = new JPanel();
		InputPanel.add(HeightPanel);
		HeightPanel.setLayout(new BoxLayout(HeightPanel, BoxLayout.LINE_AXIS));
		JLabel EnterHeight = new JLabel();
		HeightPanel.add(EnterHeight);
		EnterHeight.setText("Height: ");
		HeightPanel.add(HeightIn);
		
		JPanel WidthPanel = new JPanel();
		InputPanel.add(WidthPanel);
		WidthPanel.setLayout(new BoxLayout(WidthPanel, BoxLayout.LINE_AXIS));
		JLabel EnterWidth = new JLabel();
		WidthPanel.add(EnterWidth);
		EnterWidth.setText("Width: ");
		WidthPanel.add(WidthIn);
		
		JPanel SpeedoPanel = new JPanel();
		InputPanel.add(SpeedoPanel);
		SpeedoPanel.setLayout(new BoxLayout(SpeedoPanel, BoxLayout.LINE_AXIS));
		JLabel ShowSpeed = new JLabel();
		SpeedoPanel.add(ShowSpeed);
		ShowSpeed.setText("Speed (fps): ");
		SpeedoPanel.add(SetSpeed);
		
		JPanel DensityPanel = new JPanel();
		InputPanel.add(DensityPanel);
		DensityPanel.setLayout(new BoxLayout(DensityPanel, BoxLayout.LINE_AXIS));
		JLabel ShowDensity = new JLabel();
		DensityPanel.add(ShowDensity);
		ShowDensity.setText("Density (%)");
		DensityPanel.add(SetDensity);
		
		//Configure Height Input
		HeightIn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!HeightIn.getText().isEmpty()) {
						hei = Integer.parseInt(HeightIn.getText());
						ResetBoard();
						ToPaint();
						numOfSteps = 0;
						ShowSteps.setText("Iteration: " + numOfSteps);
						HeightIn.setText("");
					}
				}
			}
		});
		
		//Configure Width Input
		WidthIn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!WidthIn.getText().isEmpty()) {
						wid = Integer.parseInt(WidthIn.getText());
						ResetBoard();
						ToPaint();
						numOfSteps = 0;
						ShowSteps.setText("Iteration: " + numOfSteps);
						WidthIn.setText("");
					}
				}
			}
		});
		
		//Configure Speed Slider
		SetSpeed.setMajorTickSpacing(1);
		SetSpeed.setPaintTicks(true);
		SetSpeed.setPaintLabels(true);
		SetSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!SetSpeed.getValueIsAdjusting()) {
					Speed = 1000 / SetSpeed.getValue();
					timer.cancel();
					timer = new Timer();
					TimerTask task = new TimerTask() {
						public void run() {
							runGame();
						}
					};
					timer.scheduleAtFixedRate(task,  0,  Speed);
				}
			}
		});
		
		//Configure Density Slider 
		SetDensity.setMajorTickSpacing(10);
		SetDensity.setMinorTickSpacing(1);
		SetDensity.setPaintTicks(true);
		SetDensity.setPaintLabels(true);
		SetDensity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!SetDensity.getValueIsAdjusting()) {
					PercentageOfLive = SetDensity.getValue();
				}
			}
		});
		
	//COLOR PANEL (Buttons to choose color)
		JPanel BlankChoice = new JPanel();
		ColorPanel.add(BlankChoice);
		
		//Choice for background/ dead cells
		JPanel BackChoice = new JPanel();
		BackChoice.setLayout(new BoxLayout(BackChoice, BoxLayout.PAGE_AXIS));
		ColorPanel.add(BackChoice);
		
		JLabel backLabel = new JLabel();
		BackChoice.add(backLabel);
		backLabel.setText("Background Color");
		
		JButton WhiteButton = new JButton();
		BackChoice.add(WhiteButton);
		WhiteButton.setText("White");
		WhiteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				backCol = new Color(245, 245, 245);
				ToPaint();
				}
		});
		
		JButton LightGrayButton = new JButton();
		BackChoice.add(LightGrayButton);
		LightGrayButton.setText("Light Gray");
		LightGrayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				backCol = Color.LIGHT_GRAY;
				ToPaint();
				}
		});
		
		JButton GrayButton = new JButton();
		BackChoice.add(GrayButton);
		GrayButton.setText("Gray");
		GrayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				backCol = Color.GRAY;
				ToPaint();
				}
		});
		
		JButton BrownButton = new JButton();
		BackChoice.add(BrownButton);
		BrownButton.setText("Brown");
		BrownButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				backCol = new Color(51, 28, 0);
				ToPaint();
				}
		});
		
		JButton BlackButton = new JButton();
		BackChoice.add(BlackButton);
		BlackButton.setText("Black");
		BlackButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				backCol = new Color(50, 50, 50);
				ToPaint();
				}
		});
		
		//Choice for living cells
		JPanel CellChoice = new JPanel();
		CellChoice.setLayout(new BoxLayout(CellChoice, BoxLayout.PAGE_AXIS));
		ColorPanel.add(CellChoice);
		
		JLabel cellLabel = new JLabel();
		CellChoice.add(cellLabel);
		cellLabel.setText("Cell Color");
		
		JButton GreenButton = new JButton();
		CellChoice.add(GreenButton);
		GreenButton.setText("Green");
		GreenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cellCol = Color.GREEN;
				ToPaint();
				}
		});
		
		JButton CyanButton = new JButton();
		CellChoice.add(CyanButton);
		CyanButton.setText("Cyan");
		CyanButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cellCol = Color.CYAN;
				ToPaint();
				}
		});
		
		JButton YellowButton = new JButton();
		CellChoice.add(YellowButton);
		YellowButton.setText("Yellow");
		YellowButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cellCol = Color.YELLOW;
				ToPaint();
				}
		});
		
		JButton OrangeButton = new JButton();
		CellChoice.add(OrangeButton);
		OrangeButton.setText("Orange");
		OrangeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cellCol = Color.ORANGE;
				ToPaint();
				}
		});
		
		JButton RedButton = new JButton();
		CellChoice.add(RedButton);
		RedButton.setText("Red");
		RedButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cellCol = Color.RED;
				ToPaint();
				}
		});
		
		
	//..........GRIDPANEL..........//
		getContentPane().add(GridPanel);
		BeforeOut = createImage(GridPanel.getWidth(), GridPanel.getHeight());
		GridPanel.setBackground(Color.GRAY);
		
		//Control Resize
		GridPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				ToPaint();
			}
		});
		
		//Switch a cell in Custom Mode
		GridPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = hei * e.getY() / GridPanel.getHeight();
				int j = wid * e.getX() / GridPanel.getWidth(); 
				if (FreeMode) {
					currMove[i][j] = !currMove[i][j];
					ToPaint();
				}
			}
		});
		
	//..........SPLITPANE..........//
		getContentPane().add(split);
		split.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(this.getWidth() / 4);
		split.setTopComponent(ControlPanel);
		split.setBottomComponent(GridPanel);
		
	//..........TIMER..........//
		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				runGame();
			}
		};
		timer.scheduleAtFixedRate(task,  0,  Speed);
	}
	
	
	//.....................METHODS.....................//
	
	//Switch the Start Button
	protected void SwitchStartStop() {
		if (!Start) StartButton.setText("Start");
		else StartButton.setText("Pause");
	} 
	
	//Switch Custom Mode
	protected void SwitchFree() {
		if (FreeMode) SetFree.setText("Exit Custom Mode");
		else SetFree.setText("Enter Custom Mode");
	}
	
	//Draw image on Grid Panel
	protected void ToPaint() {
		BeforeOut = createImage(GridPanel.getWidth(), GridPanel.getHeight()); 
		Grid = BeforeOut.getGraphics();
		Grid.setColor(backCol);
		Grid.fillRect(0,  0, GridPanel.getWidth(), GridPanel.getHeight());
		
				//Fill living cells for grid
		Grid.setColor(cellCol);
		for (int i = 0; i < hei; ++i) {
			for (int j = 0; j < wid; ++j) {
				if (currMove[i][j]) {
					int x = j * GridPanel.getWidth() / wid;
					int y = i * GridPanel.getHeight() / hei;
					Grid.fillRect(x,  y,  GridPanel.getWidth() / wid, GridPanel.getHeight() / hei);
				}
			}
		}
				//Draw lines for grid
		Grid.setColor(Color.BLACK);
		for (int i = 0; i < hei; ++i) {
			int y = i * GridPanel.getHeight() / hei;
			Grid.drawLine(0,  y,  GridPanel.getWidth(), y);
		}
		for (int j = 0; j < wid; ++j) {
			int x = j * GridPanel.getWidth() / wid;
			Grid.drawLine(x, 0, x, GridPanel.getHeight());
		}
		GridPanel.getGraphics().drawImage(BeforeOut, 0, 0, GridPanel);
	}
	
	//Decide if a cell lives or dies next
	protected boolean LiveOrDie(int i, int j) {
		int neigh = 0;
		if (j > 0) {
			if (currMove[i][j-1]) neigh++;
			if (i > 0) {
				if (currMove[i-1][j-1]) neigh++;
			}
			if (i < hei - 1) {
				if (currMove[i+1][j-1]) neigh++;
			}
		}
		if (j < wid - 1) {
			if (currMove[i][j+1]) neigh++;
			if (i > 0) {
				if (currMove[i-1][j+1]) neigh++;
			}
			if (i < hei - 1) {
				if (currMove[i+1][j+1]) neigh++;
			}
		}
		if (i > 0) {
			if (currMove[i-1][j]) neigh++;
		}
		if (i < hei - 1) {
			if (currMove[i+1][j]) neigh++;
		}
			//If Wrap mode is activated
		if (wrapped) { 
			if (j == 0) {
				if (currMove[i][wid - 1]) neigh++;
				if (i > 0) {if (currMove[i-1][wid -1]) neigh++;}
				if (i == 0) {
					if (currMove[hei - 1][wid - 1]) neigh++;
					if (currMove[hei - 1][0]) neigh++;
					if (currMove[hei - 1][1]) neigh++;
				}
				if (i < hei - 1) {if (currMove[i+1][wid - 1]) neigh++;}
				if (i == hei - 1) {
					if (currMove[0][wid - 1]) neigh++;
					if (currMove[0][0]) neigh++;
					if (currMove[0][1]) neigh++;
				}
			}
			else if (j == wid - 1) {
				if (currMove[i][0]) neigh++;
				if (i > 0) {if (currMove[i-1][0]) neigh++;}
				if (i == 0) {
					if (currMove[hei - 1][0]) neigh++;
					if (currMove[hei - 1][wid - 1]) neigh++;
					if (currMove[hei - 1][wid - 2]) neigh++;
				}
				if (i < hei - 1) {if (currMove[i+1][0]) neigh++;}
				if (i == hei - 1)  {
					if (currMove[0][0]) neigh++;
					if (currMove[0][wid - 1]) neigh++;
					if (currMove[0][wid - 2]) neigh++;
				}
			}
			else {
				if (i == 0) {
					if (currMove[hei - 1][j-1]) neigh++;
					if (currMove[hei - 1][j]) neigh++;
					if (currMove[hei - 1][j+1]) neigh++;
				}
				if (i == hei - 1) {
					if (currMove[0][j-1]) neigh++;
					if (currMove[0][j]) neigh++;
					if (currMove[0][j+1]) neigh++;
				}
			}
		} 
		return ((neigh == 3) || ((neigh == 2) && currMove[i][j]));
	} 
	
	//Run the game
	protected void runGame() {
		if (Start) {
			numOfSteps++;
			for (int i = 0; i < hei; ++i) {
				for (int j = 0; j < wid; ++j) {
					newMove[i][j] = LiveOrDie(i, j);
				}
			}
			for (int i = 0; i < hei; ++i) {
				for (int j = 0; j < wid; ++j) {
					currMove[i][j] = newMove[i][j];
				}
			}
			ShowSteps.setText("Iteration: " + numOfSteps);
			ToPaint();
		}
	} 
	
	//Reset board
	protected void ResetBoard() {
		currMove = new boolean[hei][wid];
		newMove = new boolean[hei][wid];
	}
	
	//Randomize board 
	protected void RandomizeBoard() {
		currMove = new boolean[hei][wid];
		newMove = new boolean[hei][wid];
		for (int i = 0; i < hei; ++i) {
			for (int j = 0; j < wid; ++j) {
				currMove[i][j] = RandBool();
			}
		}
	} 
	
	//Randomize the initial position
	protected boolean RandBool() {
		Random Rand = new Random();
		return (Double.compare(Rand.nextDouble(), (double) PercentageOfLive / 100) < 0);
	} 
	
	//Main method 
	public static void main(String[] args) {
		new GameOfLife().setVisible(true);
	}
}