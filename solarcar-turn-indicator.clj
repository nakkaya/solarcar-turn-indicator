(configure-ferret! :command "mv solarcar-turn-indicator.cpp solarcar-turn-indicator.ino")
(configure-runtime! FERRET_DISABLE_OUTPUT_STREAM true
                    FERRET_MEMORY_POOL_SIZE 756
                    FERRET_MEMORY_POOL_PAGE_TYPE char
                    FERRET_PROGRAM_MAIN program)

(def left-input 7)
(def right-input 8)
(def left-output 9)
(def right-output 10)
(def debug-pin 13)

(pin-mode left-input   :input)
(pin-mode right-input  :input)
(pin-mode left-output  :output)
(pin-mode right-output :output)
(pin-mode debug-pin    :output)

(defn flash-indicator [input output]
  (digital-write output :high)
  (digital-write debug-pin :high)
  (sleep 500)
  (digital-write output :low)
  (digital-write debug-pin :low)
  (sleep 500))

(def program
  (let [left-turn? (fn [] (digital-read left-input))
        right-turn? (fn [] (digital-read right-input))
        go-to (fn [] true)]
    
    (state-machine 
     (states
      (signal (digital-write debug-pin :low)
              (digital-write left-output :low)
              (digital-write right-output :low))
      
      (turn-left  (flash-indicator left-input left-output))
      (turn-right (flash-indicator right-input right-output)))
     
     (transitions
      (signal      left-turn?   turn-left
                   right-turn?  turn-right)
      (turn-left   go-to        signal)
      (turn-right  go-to        signal)))))
