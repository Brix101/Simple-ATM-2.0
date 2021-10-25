import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate():String{
    val sdf = SimpleDateFormat("MMM-dd-yyyy  hh:mm aa")
    return sdf.format(Date())
}


open class Customer(var accountNumber:Int = 0, var pin: Int = 0, var name: String= "", var balance: Double =0.00 ,var isClose: Boolean = false, var history: ArrayList<TransactionHistory> ){
//    function to create transaction history
    fun newTransaction(amount:Double,type:String){
        this.history.add(TransactionHistory(getCurrentDate(),amount,type))
    }

    override fun toString(): String {
        return "accountNumber=$accountNumber, pin=$pin, name='$name', balance=$balance, isClose=$isClose"
    }


}
class TransactionHistory(var date:String = "",var amount: Double =0.00,var type: String="") {
//    return transaction history
    override fun toString(): String {
        return "date='$date', amount=$amount, type='$type'"
    }
}

fun List<Customer>.filterByPin(pin: Int) = this.filter { it.pin == pin } //filter and find Customer By Pin
fun List<Customer>.filterByAccountNum(accountNumber: Int) = this.filter { it.accountNumber == accountNumber } //filter and find Customer By Account Number


var CustomerList = listOf(
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
    Customer(34567,3030,"John Doe" ,24000.00,false,arrayListOf(
        TransactionHistory("Oct-17-2021  11:21 PM",50400.00,"Deposit"),
        TransactionHistory("Oct-20-2021  12:21 PM",5040.00,"withdraw"),
        TransactionHistory("Oct-25-2021  06:21 PM",10200.00,"Sent to bla2x"))
    ),
)
fun rand(start: Int, end: Int): Int {
    require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
    return Random(System.nanoTime()).nextInt(end - start + 1) + start
}

fun main(){
    ATM().main()
//    var customer =  CustomerList.filter { it.accountNumber==rand(1, 99999) }
//        .forEach { return}
//    println(customer)
}


class ATM : Algorithm(){
    private var isNum = false
    private var pin=""
    private var choice=""
    fun main(){
        println("-----Simple ATM-----")
            while (!isNum){
                when(choice){
                    "1" -> customerLogin()
                    "2" -> openAccount()
                    "" -> {
                        println(" 1: Login | 2: Open Account")
                        choice = readLine().toString()
                    }
                }
            }
        }
    private fun customerLogin(){
        try{
            if(pin==""){
                println("please type 'cancel' to cancel")
                println("Enter User Pin :")
                pin = readLine().toString()
                if(pin=="cancel"){
                    pin = ""
                    choice=""
                    return
                }
            }else {

                val customer = loginPin(pin.toInt());
                if(customer!=null){
                    try {
                        toPrint() // to Print Choices
                        val operation = Integer.valueOf(readLine())
                        performOp(operation,customer)
                    }catch (e: Exception){
                        println("Please Select On Choices")
                    }
                }
            }
        }catch(e: Exception){
            pin = ""
            println("User Not Found")
        }
    }

//    Function to Perform Operation
    private fun performOp(operator: Any, customer: Customer){
        when(operator){
            1 -> withdraw(customer)
            2 -> deposit(customer)
            3 -> println("Your Balance :₱ ${retrieveBalance(customer)}")
            4 -> sendMoney(customer)
            5-> closeAccount(customer)
            6 -> transactionHistory(customer)
            7 -> exit()
        }
    }

    private fun exit(){
        println("Exiting...")
        pin=""
        choice=""
    }

}
//Choices to Print
fun toPrint(){
    println("Choose the operation you want to perform:")
    println(" 1: Withdraw | 2: Deposit | 3: Balance | 4: Send | 5: Close Account | 6: History | 7: Logout ")
}

open class Algorithm{
//    function to get Customer using pin
    open fun loginPin(pin: Int):Customer {
        return CustomerList.filterByPin(pin).last()
    }

    fun openAccount() {
        println("Open Account")
    }
    fun closeAccount(customer: Customer) {
        var pin = ""
        println("Please type 'cancel' to cancel")
        println("Please Enter Pin")
        pin = readLine().toString()
        if(pin=="cancel"){
            pin = ""
            return
        }
        if (customer.pin == pin.toInt()){
            customer.isClose = true
        }
        println(customer.toString())
    }
    open fun withdraw(customer: Customer) {
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
                    println("Please Input Amount Lower Than Your Balance :")
                }
            } catch (e: Exception) {
                println("Please Input Amount :")
            }
        }
    }
    open fun deposit(customer: Customer){
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
                println("Please Input Amount :")
            }
        }
    }
    open fun retrieveBalance(customer: Customer): String {
        return customer.balance.toString()
    }
    open fun transactionHistory(customer: Customer){
        println("-----Your Transaction History-----")
        val arr = customer.history
        for (i in arr.indices) {
            println(arr[i].toString())
        }
    }
    open fun sendMoney(customer: Customer) {
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
                            accountNumber = ""
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
                                    println("₱ $amount Transfer to ${customer2.name} Successfully")
                                    println("Your Balance :₱ $balance")
                                    isAccountNumber = true
                                }else{
                                    println("Please Input Amount Lower Than Your Balance :")
                                }
                            }catch (e: Exception){
                                println("Please Input A Number")
                            }
                        }else{
                            println("Account Name of ${customer2.accountNumber} is Incorrect")
                            accountName=""
                        }
                    }
                }
            }catch (e: Exception){
                println("User Not Found")
                accountNumber=""
            }
        }
    }

}

