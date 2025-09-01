export type BalanceData = { balance: number; holdBalance: number };
export type PersonalOwnerData = { firstName: string; middleName?: string; lastName: string; bornDate: number; address: string };
export type CompanyData = { registrationID: string; companyID: number; companyName: string };
export type BankUser = { registrationID: string; ownerData: PersonalOwnerData; balanceData: BalanceData; iban: string };
export type BankLegalUser = { mainData: BankUser; companyData: CompanyData };
export type Transaction = { sender: string; recipient: string; sum: number; transactionDate: number };