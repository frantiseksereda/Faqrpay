# Jak používat FaqrPay (Návod)

FaqrPay je jednoduchý nástroj pro generování platebních QR kódů a ověřování plateb v reálném čase.

### Krok 1: Prvotní nastavení
1. **Zabezpečení:** Při prvním spuštění si vytvořte heslo. Tímto heslem se šifruje vaše lokální úložiště v telefonu.
2. **API Token:** Přihlaste se do Fio Internetbankingu, jděte do Nastavení -> API a vytvořte si **"Pouze pro čtení"** (Read-only) token.
3. **Aktivace:** Vložte token do Nastavení v aplikaci FaqrPay. Aplikace automaticky načte číslo vašeho účtu (IBAN).

### Krok 2: Generování platby
1. Přejděte na záložku **Platba (QR)**.
2. Zadejte částku (Zatím jsou podporovány pouze platby v Kč)
3. Klepněte na **Generovat QR kód**. Vytvoří se standardní SPD QR kód.

### Krok 3: Ověření platby
1. Jakmile plátce naskenuje kód a odešle peníze, počkejte několik sekund.
2. Klepněte na **Ověřit zda dorazilo**.
3. Aplikace zkontroluje váš Fio účet. Pokud najde unikátní ID transakce, stav se změní na **Zaplaceno (✅)**.

### Krok 4: Historie
- Všechny platby vidíte v záložce **Historie**.
- Platby jsou seskupeny podle dnů a u každého dne vidíte celkový součet úspěšných plateb.
-----

# How to Use FaqrPay

FaqrPay is a simple utility to generate payment QR codes and verify their payment status in real-time.

### Step 1: Initial Setup
1. **Security:** On the first launch, create a password. This password encrypts your local storage.
2. **API Token:** Log in to your Fio Internet Banking, go to Settings -> API, and create a **Read-only** token.
3. **Activation:** Paste the token into FaqrPay's Settings. The app will automatically fetch your account number (IBAN).

### Step 2: Generating a Payment
1. Go to the **Platba (QR)** tab.
2. Enter the amount (Only CZK supported so far).
3. Tap **Generovat QR kód**. A standard SPD QR code is created.

### Step 3: Validating a Payment
1. Once the customer/friend scans the QR and sends the money, wait a few seconds.
2. Tap **Ověřit zda dorazilo**.
3. The app checks your Fio account. If the unique transaction ID is found, the status changes to **Paid (✅)**.

### Step 4: History
- View all your generated payments in the **Historie** tab.
- Payments are grouped by day with a total sum of successful transactions.
