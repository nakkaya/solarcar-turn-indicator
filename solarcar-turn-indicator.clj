(configure-ferret! :command "mv solarcar-turn-indicator.cpp solarcar-turn-indicator.ino")
(configure-runtime! FERRET_DISABLE_OUTPUT_STREAM true
                    FERRET_MEMORY_POOL_SIZE 256
                    FERRET_MEMORY_POOL_PAGE_TYPE char
                    FERRET_PROGRAM_MAIN program)

(def hazard-input 7)
(def left-input   7)
(def right-input  8)
(def left-output  9)
(def right-output 10)
(def debug-pin    13)

(pin-mode hazard-input :input)
(pin-mode left-input   :input)
(pin-mode right-input  :input)
(pin-mode left-output  :output)
(pin-mode right-output :output)
(pin-mode debug-pin    :output)

(defn turn-off-output []
  (digital-write debug-pin    :low)
  (digital-write left-output  :low)
  (digital-write right-output :low))

(defn flash-indicator [output]
  (digital-write output    :high)
  (digital-write debug-pin :high)
  (sleep 500)
  (digital-write output    :low)
  (digital-write debug-pin :low)
  (sleep 500))

(defn flash-hazard []
  (digital-write left-output  :high)
  (digital-write right-output :high)
  (digital-write debug-pin    :high)
  (sleep 500)
  (digital-write left-output  :low)
  (digital-write right-output :low)
  (digital-write debug-pin    :low)
  (sleep 500))

(def program
  (state-machine 
   (states
    (signal     (turn-off-output))
    (turn-left  (flash-indicator left-output))
    (turn-right (flash-indicator right-output))
    (hazard     (flash-hazard)))
   
   (transitions
    (signal      #(digital-read hazard-input) hazard
                 #(digital-read left-input)   turn-left
                 #(digital-read right-input)  turn-right)
    (turn-left   #(identity true)             signal)
    (turn-right  #(identity true)             signal)
    (hazard      #(identity true)             signal))))
