(configure-ferret! :command "mv solarcar-turn-indicator.cpp solarcar-turn-indicator.ino")
(configure-runtime! FERRET_DISABLE_OUTPUT_STREAM true
                    FERRET_MEMORY_POOL_SIZE 256
                    FERRET_MEMORY_POOL_PAGE_TYPE char
                    FERRET_PROGRAM_MAIN program)

(def left-input   7)
(def right-input  8)
(def hazard-input 9)
(def right-output 12)
(def left-output  13)

(pin-mode hazard-input :input)
(pin-mode left-input   :input)
(pin-mode right-input  :input)
(pin-mode left-output  :output)
(pin-mode right-output :output)

(defn turn-off-output []
  (digital-write left-output  :low)
  (digital-write right-output :low))

(defn flash-indicator [output]
  (digital-write output    :high)
  (sleep 500)
  (digital-write output    :low)
  (sleep 500))

(defn flash-hazard []
  (digital-write left-output  :high)
  (digital-write right-output :high)
  (sleep 500)
  (digital-write left-output  :low)
  (digital-write right-output :low)
  (sleep 500))

(def program
  (state-machine 
   (states
    (signal     (turn-off-output))
    (turn-left  (flash-indicator left-output))
    (turn-right (flash-indicator right-output))
    (hazard     (flash-hazard)))
   
   (transitions
    (signal      #(digital-read left-input)   turn-left
                 #(digital-read right-input)  turn-right
                 #(digital-read hazard-input) hazard)
    (hazard      #(identity true)             signal)
    (turn-left   #(identity true)             signal)
    (turn-right  #(identity true)             signal))))
