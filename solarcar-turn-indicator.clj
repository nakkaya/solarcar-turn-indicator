(configure-ferret! :command "mv solarcar-turn-indicator.cpp solarcar-turn-indicator.ino")
(configure-runtime! FERRET_DISABLE_OUTPUT_STREAM true
                    FERRET_MEMORY_POOL_SIZE 256
                    FERRET_MEMORY_POOL_PAGE_TYPE char
                    FERRET_PROGRAM_MAIN program)

(require '[ferret.arduino :as gpio])

(def left-input   7)
(def right-input  8)
(def hazard-input 9)
(def right-output 12)
(def left-output  13)

(gpio/pin-mode hazard-input :input)
(gpio/pin-mode left-input   :input)
(gpio/pin-mode right-input  :input)
(gpio/pin-mode left-output  :output)
(gpio/pin-mode right-output :output)

(defn turn-off-output []
  (gpio/digital-write left-output  :low)
  (gpio/digital-write right-output :low))

(defn flash-indicator [output]
  (gpio/digital-write output    :high)
  (sleep 500)
  (gpio/digital-write output    :low)
  (sleep 500))

(defn flash-hazard []
  (gpio/digital-write left-output  :high)
  (gpio/digital-write right-output :high)
  (sleep 500)
  (gpio/digital-write left-output  :low)
  (gpio/digital-write right-output :low)
  (sleep 500))

(def program
  (state-machine 
   (states
    (signal     (turn-off-output))
    (turn-left  (flash-indicator left-output))
    (turn-right (flash-indicator right-output))
    (hazard     (flash-hazard)))
   
   (transitions
    (signal      #(gpio/digital-read left-input)    turn-left
                 #(gpio/digital-read right-input)   turn-right
                 #(gpio/digital-read hazard-input)  hazard)
    (hazard      #(identity true)                   signal)
    (turn-left   #(identity true)                   signal)
    (turn-right  #(identity true)                   signal))))
