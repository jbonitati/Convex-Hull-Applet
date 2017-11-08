# Convex-Hull-Applet
An interactive, graphically animated Java applet to aid in the visualization of convex hull algorithms.

Author: Joey Bonitati

## Using the Applet Interface
### Choose your algorithm
In the menu bar of the applet, there is a dropdown menu labeled "Algorithm". Here you can select which convex hull algorithm will be used in the animation.
- **Naive**: A brute force algorithm. It takes every pair of points and tests whether the line between them is on the hull by checking if all of the other points are on the same side of the line.
- **Gift Wrapping (AKA Jarvis March)**: [https://en.wikipedia.org/wiki/Gift_wrapping_algorithm](https://en.wikipedia.org/wiki/Gift_wrapping_algorithm)
- **Graham Scan**: [https://en.wikipedia.org/wiki/Graham_scan](https://en.wikipedia.org/wiki/Graham_scan)
- **Quickhull**: [https://en.wikipedia.org/wiki/Quickhull](https://en.wikipedia.org/wiki/Quickhull)
- **Incremental**: [Algorithm by Michael Kallay (1984)](https://www.researchgate.net/publication/220114226_The_Complexity_of_Incremental_Convex_Hull_Algorithms_in_R). This algorithm starts with the convex hull of the three leftmost points and recalculates the hull after adding each point in the set one-by-one.
- **Kirkpatrick-Seidel**: [The Ultimate Convex Hull algorithm by Kirkpatrick and Seidel](https://en.wikipedia.org/wiki/Kirkpatrick%E2%80%93Seidel_algorithm). An alternative to "divide-and-conquer" called "marriage-before-conquest"

### Set the running speed
In the menu bar, there is a dropdown menu labeled "Speed". Change this to change the speed at which the algorithm runs. If you choose "Step by step", you must use the "Step" button at the bottom of the display to run through the algorithm.

### Adding points
There are two ways to add points to calculate the convex hull for:
- Click anywhere in the center display to add a point where your mouse is pointing
- Use the "add x points" button at the top of the display to add the number of points which is entered into the "x = _____" box to the left of this button.

### Running the Algorithm
Use the "Start" button at the top of the display to start whichever algorithm is selected in the menu bar.

Control the display of the algorithm with the "Speed" options in the menu bar or the "Pause", "Play", and "Stop" buttons at the bottom of the display.

The "Start" button will clear any existing lines on the display, but it will not clear the points in the set. To remove all of the points from the display, use the "Clear points" button.
