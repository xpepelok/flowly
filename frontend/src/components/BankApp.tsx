import React, { useState } from "react";
import UserTab from "./UserTab";
import TransactionTab from "./TransactionTab";

type TabKey = "users" | "transactions";

export default function BankApp() {
    const [tab, setTab] = useState<TabKey>("users");

    return (
        <div className="min-h-screen bg-gray-50 text-gray-900">
            <header className="border-b bg-white">
                <div className="mx-auto max-w-6xl px-4 py-4 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <img src="/logo.svg" alt="Flowly logo" className="w-12 h-12" />
                        <h1 className="text-xl font-semibold">Flowly Admin Panel</h1>
                    </div>

                    {/* Навигация */}
                    <nav className="flex gap-2">
                        <button
                            className={`px-3 py-1 rounded ${
                                tab === "users" ? "bg-gray-900 text-white" : "bg-gray-200"
                            }`}
                            onClick={() => setTab("users")}
                        >
                            Users
                        </button>
                        <button
                            className={`px-3 py-1 rounded ${
                                tab === "transactions" ? "bg-gray-900 text-white" : "bg-gray-200"
                            }`}
                            onClick={() => setTab("transactions")}
                        >
                            Transactions
                        </button>
                    </nav>
                </div>
            </header>

            <main className="mx-auto max-w-6xl px-4 py-6">
                {tab === "users" ? <UserTab /> : <TransactionTab />}
            </main>
        </div>
    );
}
