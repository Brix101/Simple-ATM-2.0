import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getCurrentDate():String{
    val sdf = SimpleDateFormat("MMM-dd-yyyy  hh:mm aa")
    return sdf.format(Date())
}


open class Customer(var accountNumber:Int = 0, var pin: Int = 0, var name: String= "", var balance: Double =0.00 ,var isClose: Boolean = false, var history: ArrayList<TransactionHistory> ){
//    function to create transaction history
    fun newTransaction(amount:Double,type:String){
        this.history.add(TransactionHistory(getCurrentDate(),amount,type))
    }
//  return Customer as String
    override fun toString(): String {
        return "accountNumber=$accountNumber, pin=$pin, name='$name', balance=$balance, isClose=$isClose, history=$history"
    }
    //  return Customer as String
     fun getUserData(): String {
        return "accountNumber=$accountNumber, pin=$pin, name='$name'"
    }
}

class TransactionHistory(var date:String = "",var amount: Double =0.00,var type: String="") {
//    return transaction history
    override fun toString(): String {
        return "date='$date', amount=$amount, type='$type'"
    }
}

fun ArrayList<Customer>.filterByPin(pin: Int) = this.filter { it.pin == pin } //filter and find Customer By Pin
fun ArrayList<Customer>.filterByAccountNum(accountNumber: Int) = this.filter { it.accountNumber == accountNumber } //filter and find Customer By Account Number


var CustomerList = arrayListOf(
    Customer(12345,1010,"Brixter Porras",5000.00,false,arrayListOf(
            TransactionHistory("Oct-15-2021  06:21 PM",5000.00,"Deposit"),
            TransactionHistory("Oct-18-2021  12:21 PM",500.00,"Withdraw"),
            TransactionHistory("Oct-25-2021  06:21 PM",1000.00,"Sent to bla2x"))
    ),
    Customer(23456,2020,"Jane Doe" ,76000.00,false,arrayListOf(
        TransactionHistory("Oct-12-2021  08:21 PM",45000.00,"Deposit"),
        TransactionHistory("Oct-23-2021  10:21 PM",5050.00,"Withdraw"),
        TransactionHistory("Oct-25-2021  06:21 PM",2000.00,"Sent to bla2x"))
    ),
    Customer(34567,3030,"John Doe" ,24000.00,true,arrayListOf(
        TransactionHistory("Oct-17-2021  11:21 PM",50400.00,"Deposit"),
        TransactionHistory("Oct-20-2021  12:21 PM",5040.00,"withdraw"),
        TransactionHistory("Oct-25-2021  06:21 PM",10200.00,"Sent to bla2x"))
    ),
)

fun main(){
    ATM().main()
}

class ATM : Algorithm(){
    private var isNum = false
    private var userPin=""
    private var transactionCode=""
    fun main(){
        println("-----Simple ATM-----")
            while (!isNum){
                when(transactionCode){
                    "1" -> customerLogin()
                    "2" -> transactionCode = openNewAccount()
                    else -> {
                        println("1: Login | 2: Open New Account ")
                        transactionCode = readLine().toString()
                    }
                }
            }
    }
    private fun customerLogin(){
        try{
            if(userPin==""){
                println("please type 'cancel' to cancel")
                println("Enter User Pin :")
                userPin = readLine().toString()
                if(userPin=="cancel"){
                    userPin = ""
                    transactionCode=""
                    return
                }
            }else {
                val customer = loginPin(userPin.toInt())
                if(customer!=null){
                    if (customer.isClose){
                        val isOpen = openExistingAccount()
                        if(isOpen){
                            customer.isClose = false
                        }else{
                            transactionCode=""
                            userPin = ""
                        }
                    }else{
                        try {
                            toPrint(customer.name.toString()) // to Print Choices
                            val transaction = Integer.valueOf(readLine())
                            userTransaction(transaction,customer)
                        }catch (e: Exception){
                            println("Please Type the Number Of your Transaction")
                        }
                    }
                }else{
                    userPin = ""
                    println("User Not Found")
                }
            }
        }catch(e: Exception){
            userPin = ""
            println("User Not Found")
        }
    }

//    Function to Perform Transaction
    private fun userTransaction(transaction: Any, customer: Customer){
        when(transaction){
            1 -> withdraw(customer)
            2 -> deposit(customer)
            3 -> println("Your Balance :₱ ${retrieveBalance(customer)}")
            4 -> sendMoney(customer)
            5 -> println(customer.getUserData())
            6 -> closeAccount(customer)
            7 -> transactionHistory(customer)
            8 -> exit()
        }
    }

    private fun exit(){
        println("Exiting...")
        userPin=""
        transactionCode=""
    }

}

open class Algorithm{
    //    function to get Customer using pin
    fun loginPin(pin: Int): Customer? {
        return CustomerList.filterByPin(pin).firstOrNull()
    }
//    function to Open newAccount
    fun openNewAccount(): String {
        println("------New Account-----")
        val newAccount = newAccount()
        var pin:String
        var name: String
        var isString = false
        while (!isString){
            try {
                println("please type 'cancel' to cancel")
                print("Input 4 digit Number Pin: ")
                pin = readLine().toString()
                if (pin=="cancel"){
                    return ""
                }
                if (pin.length==4){
                    newAccount.pin = pin.toInt()
                    println("please type 'cancel' to cancel")
                    print("input your full name: eg John Doe: ")
                    name = readLine().toString()
                    return if (name=="cancel"){
                        ""
                    }else{
                        newAccount.name = name
                        println("New User Created")
                        ""
                    }
                }
            }catch (e: Exception){
                println("Please Select 4 digit Pin")
            }
        }
        return ""
    }

    fun closeAccount(customer: Customer) {
        println("Please type 'cancel' to cancel")
        println("Please Enter Pin")
        val pin:String = readLine().toString()
        if(pin=="cancel"){
            return
        }
        if (customer.pin == pin.toInt()){
            customer.isClose = true
        }
        println(customer.toString())
    }
    fun openExistingAccount(): Boolean{
        println("This Account is Closed")
        println("Open this Account: Y for Yes or N for No")
        val transaction = readLine().toString()
        var isOpen = false
        if(transaction.toLowerCase()=="y"){
            println("Please Reenter Pin")
            val pin = readLine().toString()
            val customer = CustomerList.filterByPin(pin.toInt()).firstOrNull()
            if (customer!=null){
                customer.isClose = false
                isOpen = true
            }
        }
        if(transaction.toLowerCase()=="n"){
            isOpen = false
        }
        return isOpen
    }
    fun withdraw(customer: Customer) {
        println("please type 'cancel' to cancel")
        println("Input Amount to Withdraw :")
        var isAmount = false
        while (!isAmount) {
            try {
                val amount = readLine()
                if(amount =="cancel"){
                    isAmount = true
                    return
                }
                val money = customer.balance
                if(money >= (amount?.toDouble()!!)){
                    val amountToDouble = amount.toDouble()
                    val balance = money -amountToDouble
                    customer.balance = balance
                    customer.newTransaction(amountToDouble,"Withdraw")
                    println("₱ $amount withdraw Successfully")
                    println("Your Balance :₱ $balance")
                    isAmount = true
                }else{
                    println("please type 'cancel' to cancel")
                    println("Please Input Amount Lower Than Your Balance :")
                }
            } catch (e: Exception) {
                println("please type 'cancel' to cancel")
                println("Please Input Amount :")
            }
        }
    }
    fun deposit(customer: Customer){
        println("please type 'cancel' to cancel")
        println("Input Amount to Deposit :")
        var isAmount = false
        while (!isAmount) {
            try {
                val amount = readLine()
                if(amount =="cancel"){
                    isAmount = true
                    return
                }
                val money = customer.balance
                val amountToDouble = (amount?.toDouble()!!)
                val balance = money + amountToDouble
                customer.balance = balance
                customer.newTransaction(amountToDouble,"Deposit")
                println("₱ $amount Deposit Successfully")
                println("Your Balance :₱ $balance")
                isAmount = true
            }catch (e: Exception){
                println("please type 'cancel' to cancel")
                println("Please Input Amount :")
            }
        }
    }
    fun retrieveBalance(customer: Customer): String {
        return customer.balance.toString()
    }
    fun transactionHistory(customer: Customer){
        println("-----Your Transaction History-----")
        val arr = customer.history
        for (i in arr.indices) {
            println(arr[i].toString())
        }
    }
    fun sendMoney(customer: Customer) {
        var isAccountNumber = false
        var accountNumber = ""
        var accountName = ""
        val money = customer.balance
        while (!isAccountNumber) {
            try {
                if(accountNumber==""){
                    println("please type 'cancel' to cancel")
                    println("Input Account Number of Receiver")
                    accountNumber = readLine().toString()
                    if(accountNumber =="cancel"){
                        isAccountNumber = true
                        return
                    }
                }else {
                    val customer2 = CustomerList.filterByAccountNum(accountNumber.toInt()).last()
                    if(accountName==""){
                        println("please type 'cancel' to cancel")
                        println("Input Account Name of Receiver")
                        accountName = readLine().toString()
                        if(accountName =="cancel"){
                            isAccountNumber = true
                            return
                        }
                    }
                    else{
                        if (customer2.name.equals(accountName, ignoreCase = true)){
                            try {
                                println("Input Amount to send to ${customer2.name}")
                                val amount = readLine()

                                if(money >= (amount?.toDouble()!!)){
                                    val balance = money - (amount.toDouble())
                                    customer2.balance = customer2.balance + amount.toDouble()
                                    customer.balance = balance
                                    customer.newTransaction((amount.toDouble()),"Transfer to ${customer2.name}")
                                    println("₱ $amount Transfer to ${customer2.name} Successfully")
                                    println("Your Balance :₱ $balance")
                                    isAccountNumber = true
                                }else{
                                    println("please type 'cancel' to cancel")
                                    println("Please Input Amount Lower Than Your Balance :")
                                }
                            }catch (e: Exception){
                                println("please type 'cancel' to cancel")
                                println("Please Input A Number")
                            }
                        }else{
                            println("Account Name of ${customer2.accountNumber} is Incorrect")
                            accountName=""
                        }
                    }
                }
            }catch (e: Exception){
                println("please type 'cancel' to cancel")
                println("User Not Found")
                accountNumber=""
            }
        }
    }
//    function to generate New User Account
    private fun newAccount(): Customer {
        val start = 1
        val end = 99999
        require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }

        val user = Customer(0,0,"",0.00,false, arrayListOf())

        var isNew = false
        while (!isNew){
            var newId = Random(System.nanoTime()).nextInt(end - start + 1) + start
//            Scan if User ID is Taken
            val isCustomer =  (CustomerList.filter { it.accountNumber == newId }).firstOrNull()
            if(isCustomer==null){
                user.accountNumber = newId
                CustomerList.add(user)
                isNew = true

            }else{
                newId = 0
                isNew = false
            }
        }
        return user
    }
    //Choices to Print
    fun toPrint(name: String){
        println("$name Please Type the Number of transaction you want to perform")
        println(" 1: Withdraw | 2: Deposit | 3: Balance | 4: Send | 5: Account Details | 6: Close Account | 7: Transaction History | 8: Logout ")
    }
}

