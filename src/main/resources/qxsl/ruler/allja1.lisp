;; ALLJA1 CONTEST DEFINED by 無線部開発班

(load "qxsl/ruler/jautil.lisp")

; multiple-band validation
(defun HiBand? it (<= 14000 (qxsl-band it) 50000))
(defun LoBand? it (<=  1900 (qxsl-band it)  7000))
(defun DgBand? it (7MHz? it))

; time validation
(defun 朝の部門? it (and (<= 09 (時刻 it) 11) (CW/PH? it) (HiBand? it)))
(defun 昼の部門? it (and (<= 13 (時刻 it) 14) (DIGIT? it) (DgBand? it)))
(defun 夜の部門? it (and (<= 16 (時刻 it) 19) (CW/PH? it) (LoBand? it)))

; validation of analog/digital sections
(defun 電信電話? it (some it 朝の部門? 夜の部門?))
(defun 総合部門? it (some it 朝の部門? 夜の部門? 昼の部門?))

;; validation of 1エリア内/1エリア外部門
(defun エリア内? it
	(and
		(現存? it)
		(cond
			((DIGIT? it) (市郡? it))
			((AREA1? it) (市郡? it))
			((AREA8? it) (支庁? it))
			((always it) (府県? it)))))

(defun エリア外? it (every it 現存? AREA1? 市郡?))

; keys for scoring
(defun CALL it
	(list
		(qxsl-call it)
		(qxsl-band it)
		(cond
			((MORSE? it) 1)
			((PHONE? it) 2)
			((DIGIT? it) 3))))
(defun MULT it
	(list
		(qxsl-band it)
		(qxsl-code it)))
(defun SOLE it null)
(defun CORP it (qxsl-name it))

; scoring
(defmacro 得点 (score calls mults names)
	`(block
		(setq mults (length (quote ,mults)))
		(setq names (length (quote ,names)))
		(ceiling (/ (* ,score mults) names))))

; validation routine
(defmacro 検査 (conds keys)
	`(lambda it
		(let it (decode it)
			(let msg (search it ,conds)
				(if (nil? msg)
					(success it 1 ,@(dolist (k keys) (list k 'it)))
					(failure it (format "無効な交信(%s)" msg)))))))

(defmacro 個検査 conds... `(検査 ,conds... (CALL MULT SOLE)))
(defmacro 団検査 conds... `(検査 ,conds... (CALL MULT CORP)))

; section names
(setq 内 "1エリア内")
(setq 外 "1エリア外")

(setq 個 "個人")
(setq 団 "団体")

(setq 電信 "電信限定")
(setq 電電 "電信電話")
(setq 離散 "デジタル")
(setq 総合 "総合")

(setq 19部門 "1.9MHz部門")
(setq 35部門 "3.5MHz部門")
(setq  7部門   "7MHz部門")
(setq 14部門  "14MHz部門")
(setq 21部門  "21MHz部門")
(setq 28部門  "28MHz部門")
(setq 50部門  "50MHz部門")
(setq LB部門 "1.9-7MHz部門")
(setq HB部門 "14-50MHz部門")

; section name concatenation
(defmacro cat (area op mode band) `(format "%s %s %s %s" ,area ,op ,mode ,band))

; contest definition
(set-contest JA1 "ALLJA1" "東大無線部" "allja1@ja1zlo.u-tokyo.org" "ja1zlo.u-tokyo.org")

; section codes
(defmacro SinHB (name test) `(add-section JA1 (cat ,@name) "Sin1" (個検査 ,@test) 得点))
(defmacro SinLB (name test) `(add-section JA1 (cat ,@name) "Sin2" (個検査 ,@test) 得点))
(defmacro SinDG (name test) `(add-section JA1 (cat ,@name) "Sin3" (個検査 ,@test) 得点))
(defmacro SinJS (name test) `(add-section JA1 (cat ,@name) "Sin4" (個検査 ,@test) 得点))
(defmacro MulAB (name test) `(add-section JA1 (cat ,@name) "Mul1" (団検査 ,@test) 得点))
(defmacro MulDG (name test) `(add-section JA1 (cat ,@name) "Mul2" (団検査 ,@test) 得点))
(defmacro MulJS (name test) `(add-section JA1 (cat ,@name) "Mul3" (団検査 ,@test) 得点))

; 1エリア内 個人 ローバンド部門
(SinLB (内 個 電信 19部門) (エリア内? SinOp? MORSE? 夜の部門? 1.9MHz?))
(SinLB (内 個 電信 35部門) (エリア内? SinOp? MORSE? 夜の部門? 3.5MHz?))
(SinLB (内 個 電電 35部門) (エリア内? SinOp? CW/PH? 夜の部門? 3.5MHz?))
(SinLB (内 個 電信  7部門) (エリア内? SinOp? MORSE? 夜の部門?   7MHz?))
(SinLB (内 個 電電  7部門) (エリア内? SinOp? CW/PH? 夜の部門?   7MHz?))
(SinLB (内 個 電信 LB部門) (エリア内? SinOp? MORSE? 夜の部門?))
(SinLB (内 個 電電 LB部門) (エリア内? SinOp? CW/PH? 夜の部門?))

; 1エリア外 個人 ローバンド部門
(SinLB (外 個 電信 19部門) (エリア外? SinOp? MORSE? 夜の部門? 1.9MHz?))
(SinLB (外 個 電信 35部門) (エリア外? SinOp? MORSE? 夜の部門? 3.5MHz?))
(SinLB (外 個 電電 35部門) (エリア外? SinOp? CW/PH? 夜の部門? 3.5MHz?))
(SinLB (外 個 電信  7部門) (エリア外? SinOp? MORSE? 夜の部門?   7MHz?))
(SinLB (外 個 電電  7部門) (エリア外? SinOp? CW/PH? 夜の部門?   7MHz?))
(SinLB (外 個 電信 LB部門) (エリア外? SinOp? MORSE? 夜の部門?))
(SinLB (外 個 電電 LB部門) (エリア外? SinOp? CW/PH? 夜の部門?))

; 1エリア内 個人 ハイバンド部門
(SinHB (内 個 電信 14部門) (エリア内? SinOp? MORSE? 朝の部門?  14MHz?))
(SinHB (内 個 電電 14部門) (エリア内? SinOp? CW/PH? 朝の部門?  14MHz?))
(SinHB (内 個 電信 21部門) (エリア内? SinOp? MORSE? 朝の部門?  21MHz?))
(SinHB (内 個 電電 21部門) (エリア内? SinOp? CW/PH? 朝の部門?  21MHz?))
(SinHB (内 個 電信 28部門) (エリア内? SinOp? MORSE? 朝の部門?  28MHz?))
(SinHB (内 個 電電 28部門) (エリア内? SinOp? CW/PH? 朝の部門?  28MHz?))
(SinHB (内 個 電信 50部門) (エリア内? SinOp? MORSE? 朝の部門?  50MHz?))
(SinHB (内 個 電電 50部門) (エリア内? SinOp? CW/PH? 朝の部門?  50MHz?))
(SinHB (内 個 電信 HB部門) (エリア内? SinOp? MORSE? 朝の部門?))
(SinHB (内 個 電電 HB部門) (エリア内? SinOp? CW/PH? 朝の部門?))

; 1エリア外 個人 ハイバンド部門
(SinHB (外 個 電信 14部門) (エリア外? SinOp? MORSE? 朝の部門?  14MHz?))
(SinHB (外 個 電電 14部門) (エリア外? SinOp? CW/PH? 朝の部門?  14MHz?))
(SinHB (外 個 電信 21部門) (エリア外? SinOp? MORSE? 朝の部門?  21MHz?))
(SinHB (外 個 電電 21部門) (エリア外? SinOp? CW/PH? 朝の部門?  21MHz?))
(SinHB (外 個 電信 28部門) (エリア外? SinOp? MORSE? 朝の部門?  28MHz?))
(SinHB (外 個 電電 28部門) (エリア外? SinOp? CW/PH? 朝の部門?  28MHz?))
(SinHB (外 個 電信 50部門) (エリア外? SinOp? MORSE? 朝の部門?  50MHz?))
(SinHB (外 個 電電 50部門) (エリア外? SinOp? CW/PH? 朝の部門?  50MHz?))
(SinHB (外 個 電信 HB部門) (エリア外? SinOp? MORSE? 朝の部門?))
(SinHB (外 個 電電 HB部門) (エリア外? SinOp? CW/PH? 朝の部門?))

; 団体 アナログ部門
(MulAB (内 団 電信 "部門") (エリア内? MulOp? MORSE? 電信電話?))
(MulAB (内 団 電電 "部門") (エリア内? MulOp? CW/PH? 電信電話?))
(MulAB (外 団 電信 "部門") (エリア外? MulOp? MORSE? 電信電話?))
(MulAB (外 団 電電 "部門") (エリア外? MulOp? CW/PH? 電信電話?))

; デジタル部門
(SinDG (内 個 離散 "部門") (エリア内? SinOp? DIGIT? 昼の部門?))
(SinDG (外 個 離散 "部門") (エリア外? SinOp? DIGIT? 昼の部門?))
(MulDG (内 団 離散 "部門") (エリア内? MulOp? DIGIT? 昼の部門?))
(MulDG (外 団 離散 "部門") (エリア外? MulOp? DIGIT? 昼の部門?))

; 総合部門
(SinJS (内 個 総合 "部門") (エリア内? SinOp? AN/DG? 総合部門?))
(SinJS (外 個 総合 "部門") (エリア外? SinOp? AN/DG? 総合部門?))
(MulJS (内 団 総合 "部門") (エリア内? MulOp? AN/DG? 総合部門?))
(MulJS (外 団 総合 "部門") (エリア外? MulOp? AN/DG? 総合部門?))
