#set terminal epslatex font 'verdana,14' color colortext
set terminal pdf font 'verdana,14' #color colortext

#set output "ex.png"
#set output "confidence_yago.tex"
#set output "conflict_neg_yago.pdf"
set output outputPlot
set grid ytics lc rgb "#505050"


set key tmargin center left horizontal   reverse noenhanced autotitle columnhead nobox 
set key invert samplen 2 spacing 1 width 0 height 0 


set auto x
#set yrange [ 0.60000 : 0.80 ] 

set ylabel 'Conflict Ratio' offset 2
set xlabel 'Top-K Rules' offset 5

set style data histogram
set style histogram cluster gap 1
set style fill solid border
set border 3 
set boxwidth 0.8


set xtics  norangelimit
set xtics   ()
set xtics nomirror
set ytics nomirror


#set style line 1 lt 1 lw 1 lc rgb "#9E9E9E"
set style line 1 lt 1 lw 1 lc rgb "#F44336"
set style line 2 lt 1 lw 1 lc rgb "#0D47A1"
set style line 3 lt 1 lw 1 lc rgb "#FFC107"
set style line 4 lt 1 lw 1 lc rgb "#009688"

set style increment user

#set lmargin 0.8 
set rmargin 0.3
#set bmargin 0.3
set tmargin 1.5



co =5



plot dataFile using 2:xtic(1) , for [i=3:co] '' using i



set output

