// src/app/models/transaction.model.ts

export interface Transaction {
    appid: number;
    transactionDate: Date;
    activity: string;
    txnId: number;
    comment?: string; // Optional
    debtAmt: number;
    creditAmt: number;
    transactionBreakup?: string; // Optional
    transactionStatus: string;
    instrument: string;
  }
  