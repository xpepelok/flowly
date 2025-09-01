import React, { useEffect, useState } from "react";
import type { Transaction } from "./TransactionTab";

interface Props {
    open: boolean;
    onClose: () => void;
    user?: {
        registrationId: string;
        iban: string;
        firstName?: string;
        middleName?: string;
        lastName?: string;
    } | null;
}

const API = {
    txByIban: (iban: string, offset: number, limit: number) =>
        `/api/transactions/${encodeURIComponent(iban)}?offset=${offset}&limit=${limit}`,
    report: (iban: string) => `/api/users/report?iban=${encodeURIComponent(iban)}`,
};

export default function UserModal({ open, onClose, user }: Props) {
    const [tx, setTx] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(false);
    const [offset, setOffset] = useState(0);
    const [limit] = useState(20);
    const [hasMore, setHasMore] = useState(false);

    const load = async () => {
        if (!user?.iban) return;
        setLoading(true);
        try {
            const res = await fetch(API.txByIban(user.iban, offset, limit));
            if (!res.ok) throw new Error(`Failed to load transactions: ${res.statusText}`);
            const data = await res.json();
            const list: Transaction[] = Array.isArray(data) ? data : Array.isArray(data?.content) ? data.content : [];
            setTx(list);
            setHasMore(list.length === limit);
        } catch (e) {
            console.error(e);
            setTx([]); setHasMore(false);
        } finally {
            setLoading(false);
        }
    };

    const downloadReport = async () => {
        if (!user?.iban) return;
        try {
            const res = await fetch(API.report(user.iban));
            if (!res.ok) throw new Error(`Failed: ${res.status}`);
            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `report-${user.iban}.xls`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (e) {
            console.error(e);
            alert("Failed to download report");
        }
    };

    useEffect(() => {
        if (!open) return;
        setOffset(0);
    }, [open]);

    useEffect(() => {
        if (!open) return;
        load();
    }, [open, offset]);

    if (!open || !user) return null;

    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <div className="bg-white rounded shadow-lg w-full max-w-3xl p-4">
                <div className="flex items-center justify-between mb-3">
                    <h3 className="text-lg font-semibold">
                        {user.lastName} {user.firstName} {user.middleName}
                    </h3>
                    <button className="px-3 py-1 rounded bg-gray-200" onClick={onClose}>Close</button>
                </div>

                <div className="text-sm mb-4">
                    <div><span className="text-gray-500">Registration ID:</span> {user.registrationId}</div>
                    <div><span className="text-gray-500">IBAN:</span> {user.iban}</div>
                </div>

                <div className="border rounded">
                    <div className="flex items-center justify-between p-3 border-b">
                        <div className="font-medium">Transactions</div>
                        <div className="flex items-center gap-2">
                            <button
                                className="px-3 py-1 rounded bg-blue-600 text-white"
                                onClick={downloadReport}
                            >
                                Export Report
                            </button>
                            <div className="flex items-center gap-2 text-xs text-gray-500">
                                <button
                                    className="px-2 py-1 rounded bg-gray-200 disabled:opacity-50"
                                    disabled={offset === 0 || loading}
                                    onClick={() => setOffset(Math.max(0, offset - limit))}
                                >
                                    Prev
                                </button>
                                <button
                                    className="px-2 py-1 rounded bg-gray-200 disabled:opacity-50"
                                    disabled={!hasMore || loading}
                                    onClick={() => setOffset(offset + limit)}
                                >
                                    Next
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="max-h-80 overflow-auto">
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
                </div>
            </div>
        </div>
    );
}
