(configure-ferret! :command "mv solarcar-turn-indicator.cpp solarcar-turn-indicator.ino")
(configure-runtime! FERRET_DISABLE_OUTPUT_STREAM true ;;save memory
                    FERRET_MEMORY_POOL_SIZE 756) ;; Allocate 1kb Heap

(def left-signal-input 7)
(def right-signal-input 8)
(def left-signal-output 9)
(def right-signal-output 10)
(def debug-pin 13)

(pin-mode left-signal-input :input)
(pin-mode right-signal-input :input)
(pin-mode left-signal-output :output)
(pin-mode right-signal-output :output)
(pin-mode debug-pin :output)
(digital-write debug-pin :low)

(defn flash-indicator [input output]
  (while (digital-read input)
    (digital-write output :high)
    (digital-write debug-pin :high)
    (sleep 500)
    (digital-write output :low)
    (digital-write debug-pin :low)
    (sleep 500))
  (digital-write output :low))

(forever
 (cond (digital-read left-signal-input)
       (flash-indicator left-signal-input left-signal-output)

       (digital-read right-signal-input)
       (flash-indicator right-signal-input right-signal-output)))
