import React, { useEffect, useMemo, useState } from "react";
import Pagination from "./Pagination";

export type Transaction = {
    sender: string;
    recipient: string;
    sum: number;
    transactionDate: number;
};

const API = {
    create: "/api/transactions",
    listByIban: (iban: string, offset: number, limit: number) =>
        `/api/transactions/${encodeURIComponent(iban)}?offset=${offset}&limit=${limit}`,
    listAll: (offset: number, limit: number) =>
        `/api/transactions?offset=${offset}&limit=${limit}`,
    count: "/api/transactions/count",
};

export default function TransactionTab() {
    const [senderIban, setSenderIban] = useState("");
    const [recipientIban, setRecipientIban] = useState("");
    const [sumInput, setSumInput] = useState("0.00");

    const [searchIban, setSearchIban] = useState("");
    const [tx, setTx] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(false);

    const [offset, setOffset] = useState(0);
    const [limit, setLimit] = useState(10);
    const [total, setTotal] = useState(0);

    const centsFromInput = () => {
        const v = parseFloat((sumInput || "0").replace(",", "."));
        return Math.max(0, Math.round((isNaN(v) ? 0 : v) * 100));
    };

    const canCreate = useMemo(
        () =>
            senderIban.trim().length > 0 &&
            recipientIban.trim().length > 0 &&
            centsFromInput() >= 1,
        [senderIban, recipientIban, sumInput]
    );

    const submit = async () => {
        if (!canCreate) return;
        setLoading(true);
        try {
            const payload: Transaction = {
                sender: senderIban.trim(),
                recipient: recipientIban.trim(),
                sum: centsFromInput(),
                transactionDate: 0,
            };

            const res = await fetch(API.create, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            if (!res.ok) {
                const text = await res.text().catch(() => "");
                throw new Error(`Create failed: ${res.status} ${text}`);
            }

            setSenderIban("");
            setRecipientIban("");
            setSumInput("0.00");
            if (searchIban.trim()) await fetchList();
            else await fetchList();
        } catch (e) {
            console.error(e);
            alert("Failed to create transaction. Please check data and try again.");
        } finally {
            setLoading(false);
        }
    };

    const fetchCount = async () => {
        try {
            const res = await fetch(API.count);
            if (!res.ok) throw new Error(`Count failed: ${res.status}`);
            const data = await res.json();
            const n = typeof data === "number" ? data : (data?.amount ?? data?.count ?? 0);
            setTotal(Number(n) || 0);
        } catch (e) {
            console.error(e);
            setTotal(0);
        }
    };

    const fetchList = async () => {
        setLoading(true);
        try {
            const url = searchIban.trim()
                ? API.listByIban(searchIban.trim(), offset, limit)
                : API.listAll(offset, limit);

            const res = await fetch(url);
            if (!res.ok) throw new Error(`List failed: ${res.status}`);
            const data = await res.json();
            setTx(Array.isArray(data) ? data : data?.content ?? []);
        } catch (e) {
            console.error(e);
            setTx([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCount();
    }, []);

    useEffect(() => {
        fetchList();
    }, [offset, limit]);

    return (
        <div className="space-y-6">
            {/* Создание транзакции */}
            <section className="bg-white border rounded p-4 space-y-3">
                <h2 className="font-semibold">Create Transaction</h2>
                <div className="grid gap-3 sm:grid-cols-3">
                    <div>
                        <label className="block text-sm mb-1">Sender IBAN *</label>
                        <input
                            className="w-full border rounded px-3 py-2"
                            placeholder="Sender IBAN"
                            value={senderIban}
                            onChange={(e) => setSenderIban(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm mb-1">Recipient IBAN *</label>
                        <input
                            className="w-full border rounded px-3 py-2"
                            placeholder="Recipient IBAN"
                            value={recipientIban}
                            onChange={(e) => setRecipientIban(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm mb-1">Amount *</label>
                        <input
                            type="number"
                            min="0.01"
                            step="0.01"
                            className="w-full border rounded px-3 py-2"
                            placeholder="0.00"
                            value={sumInput}
                            onChange={(e) => setSumInput(e.target.value)}
                        />
                    </div>
                </div>
                <button
                    className={`px-4 py-2 rounded ${canCreate ? "bg-gray-900 text-white" : "bg-gray-300"}`}
                    onClick={submit}
                    disabled={!canCreate || loading}
                >
                    Create
                </button>
            </section>

            {/* Список транзакций */}
            <section className="bg-white border rounded p-4 space-y-3">
                <h2 className="font-semibold">Transactions</h2>
                <div className="flex items-end gap-2">
                    <div className="flex-1">
                        <label className="block text-sm mb-1">Search by IBAN</label>
                        <input
                            className="w-full border rounded px-3 py-2"
                            placeholder="Enter IBAN to filter"
                            value={searchIban}
                            onChange={(e) => setSearchIban(e.target.value)}
                        />
                    </div>
                    <button
                        className="px-4 py-2 rounded bg-gray-900 text-white"
                        onClick={() => { setOffset(0); fetchList(); }}
                    >
                        Search
                    </button>
                </div>

                <div className="overflow-x-auto">
                    <table className="min-w-full text-sm">
                        <thead>
                        <tr className="border-b">
                            <th className="text-left p-2">Date</th>
                            <th className="text-left p-2">Sender</th>
                            <th className="text-left p-2">Recipient</th>
                            <th className="text-right p-2">Amount</th>
                        </tr>
                        </thead>
                        <tbody>
                        {loading ? (
                            <tr><td colSpan={4} className="p-3">Loading…</td></tr>
                        ) : tx.length === 0 ? (
                            <tr><td colSpan={4} className="p-3">No transactions found.</td></tr>
                        ) : (
                            tx.map((t, i) => (
                                <tr key={i} className="border-b">
                                    <td className="p-2">{new Date(t.transactionDate).toLocaleString()}</td>
                                    <td className="p-2">{t.sender}</td>
                                    <td className="p-2">{t.recipient}</td>
                                    <td className="p-2 text-right">{(t.sum / 100).toFixed(2)}</td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>

                <div className="pt-2">
                    <Pagination
                        total={total}
                        limit={limit}
                        offset={offset}
                        onPageChange={(page) => setOffset(page * limit)}
                    />
                </div>
            </section>
        </div>
    );
}
