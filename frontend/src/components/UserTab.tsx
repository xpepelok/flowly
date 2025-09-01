import React, { useEffect, useMemo, useState } from "react";
import Pagination from "./Pagination";
import UserModal from "./UserModal";

type SearchType = "IBAN" | "LAST_NAME";

type BankUser = {
    registrationId?: string;
    registrationID?: string;
    iban: string;
    ownerData?: {
        firstName?: string;
        middleName?: string;
        lastName?: string;
        bornDate?: number;
        address?: string;
    };
};

const API = {
    createUser: "/api/users",
    createLegal: (companyId: number, companyName: string) =>
        `/api/users/legal?companyId=${companyId}&companyName=${encodeURIComponent(companyName)}`,
    search: (type: SearchType, query: string, offset: number, limit: number) =>
        `/api/users/search?type=${type}&query=${encodeURIComponent(query)}&offset=${offset}&limit=${limit}`,
    count: "/api/users/count",
};

export default function UserTab() {
    const [firstName, setFirst] = useState("");
    const [middleName, setMiddle] = useState("");
    const [lastName, setLast] = useState("");
    const [bornDate, setBornDate] = useState("");
    const [address, setAddress] = useState("");

    const [companyId, setCompanyId] = useState(0);
    const [companyName, setCompanyName] = useState("");

    const [type, setType] = useState<SearchType>("IBAN");
    const [query, setQuery] = useState("");
    const [users, setUsers] = useState<BankUser[]>([]);
    const [loading, setLoading] = useState(false);
    const [offset, setOffset] = useState(0);
    const [limit, setLimit] = useState(10);
    const [total, setTotal] = useState(0);

    const [modalOpen, setModalOpen] = useState(false);
    const [modalUser, setModalUser] = useState<{
        registrationId: string;
        iban: string;
        firstName?: string;
        middleName?: string;
        lastName?: string;
    } | null>(null);

    const requiredOwnerFilled =
        firstName.trim().length > 0 &&
        lastName.trim().length > 0 &&
        middleName.trim().length > 0 &&
        bornDate.trim().length > 0 &&
        address.trim().length > 0;

    const canCreatePersonal = useMemo(() => requiredOwnerFilled, [requiredOwnerFilled]);
    const canCreateLegal = useMemo(
        () => companyId > 0 && companyName.trim().length > 0 && requiredOwnerFilled,
        [companyId, companyName, requiredOwnerFilled]
    );

    const ownerDataFromState = () => ({
        firstName: firstName.trim(),
        middleName: middleName.trim(),
        lastName: lastName.trim(),
        bornDate: new Date(bornDate).getTime(),
        address: address.trim(),
    });

    const submitPersonal = async () => {
        if (!canCreatePersonal) return;
        setLoading(true);
        try {
            const res = await fetch(API.createUser, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(ownerDataFromState()),
            });
            if (!res.ok) throw new Error(`Create user failed: ${res.status} ${await res.text().catch(() => "")}`);
            setFirst(""); setMiddle(""); setLast(""); setBornDate(""); setAddress("");
            setOffset(0);
            await Promise.all([fetchCount(), fetchUsers()]);
        } catch (e) {
            console.error(e); alert("Failed to create user");
        } finally {
            setLoading(false);
        }
    };

    const submitLegal = async () => {
        if (!canCreateLegal) return;
        setLoading(true);
        try {
            const res = await fetch(API.createLegal(companyId, companyName), {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(ownerDataFromState()),
            });
            if (!res.ok) throw new Error(`Create legal user failed: ${res.status} ${await res.text().catch(() => "")}`);
            setCompanyId(0); setCompanyName(""); setFirst(""); setMiddle(""); setLast(""); setBornDate(""); setAddress("");
            setOffset(0);
            await Promise.all([fetchCount(), fetchUsers()]);
        } catch (e) {
            console.error(e); alert("Failed to create legal user");
        } finally {
            setLoading(false);
        }
    };

    const fetchCount = async () => {
        try {
            const res = await fetch(API.count);
            if (!res.ok) throw new Error("Count failed");
            const data = await res.json();
            const n = typeof data === "number" ? data : (data?.amount ?? data?.count ?? 0);
            setTotal(Number(n) || 0);
        } catch {
            setTotal(0);
        }
    };

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const res = await fetch(API.search(type, query, offset, limit));
            if (!res.ok) throw new Error("Search failed");
            const data = await res.json();
            const arr: BankUser[] = Array.isArray(data) ? data : data?.users ?? [];
            setUsers(arr);
        } catch {
            setUsers([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchUsers(); }, [type, offset, limit]);
    useEffect(() => { fetchCount(); }, []);

    return (
        <div className="space-y-8">
            {/* CREATE PERSONAL */}
            <section className="bg-white border rounded p-4 space-y-3">
                <h2 className="font-semibold">Create Personal User</h2>
                <div className="grid gap-3 sm:grid-cols-3">
                    <div>
                        <label className="block text-sm mb-1">First name *</label>
                        <input className="w-full border rounded px-3 py-2" value={firstName} onChange={(e) => setFirst(e.target.value)} required />
                    </div>
                    <div>
                        <label className="block text-sm mb-1">Last name *</label>
                        <input className="w-full border rounded px-3 py-2" value={lastName} onChange={(e) => setLast(e.target.value)} required />
                    </div>
                    <div>
                        <label className="block text-sm mb-1">Middle name *</label>
                        <input className="w-full border rounded px-3 py-2" value={middleName} onChange={(e) => setMiddle(e.target.value)} required />
                    </div>
                    <div className="sm:col-span-3">
                        <label className="block text-sm mb-1">Date of Birth *</label>
                        <input type="date" className="w-full border rounded px-3 py-2" value={bornDate} onChange={(e) => setBornDate(e.target.value)} required />
                    </div>
                    <div className="sm:col-span-3">
                        <label className="block text-sm mb-1">Address *</label>
                        <input className="w-full border rounded px-3 py-2" value={address} onChange={(e) => setAddress(e.target.value)} required />
                    </div>
                </div>
                <button className={`px-4 py-2 rounded ${canCreatePersonal ? "bg-gray-900 text-white" : "bg-gray-300"}`} onClick={submitPersonal} disabled={!canCreatePersonal || loading}>
                    Create
                </button>
            </section>

            {/* CREATE LEGAL */}
            <section className="bg-white border rounded p-4 space-y-3">
                <h2 className="font-semibold">Create Legal User</h2>
                <div className="grid gap-3 sm:grid-cols-3">
                    <div>
                        <label className="block text-sm mb-1">Company ID *</label>
                        <input className="w-full border rounded px-3 py-2" type="number" value={companyId} onChange={(e) => setCompanyId(Number(e.target.value) || 0)} />
                    </div>
                    <div className="sm:col-span-2">
                        <label className="block text-sm mb-1">Company Name *</label>
                        <input className="w-full border rounded px-3 py-2" value={companyName} onChange={(e) => setCompanyName(e.target.value)} />
                    </div>
                </div>
                <button className={`px-4 py-2 rounded ${canCreateLegal ? "bg-gray-900 text-white" : "bg-gray-300"}`} onClick={submitLegal} disabled={!canCreateLegal || loading}>
                    Create Legal
                </button>
            </section>

            {/* SEARCH */}
            <section className="bg-white border rounded p-4 space-y-3">
                <h2 className="font-semibold">Search Users</h2>
                <div className="flex flex-wrap gap-3 items-end">
                    <div>
                        <label className="block text-sm mb-1">Type</label>
                        <select className="border rounded px-3 py-2" value={type} onChange={(e) => setType(e.target.value as SearchType)}>
                            <option value="IBAN">IBAN</option>
                            <option value="LAST_NAME">Last Name</option>
                        </select>
                    </div>
                    <div className="flex-1 min-w-[240px]">
                        <label className="block text-sm mb-1">Query</label>
                        <input className="w-full border rounded px-3 py-2" value={query} onChange={(e) => setQuery(e.target.value)} />
                    </div>
                    <button className="px-4 py-2 rounded bg-gray-900 text-white" onClick={() => { setOffset(0); fetchUsers(); }}>
                        Search
                    </button>
                </div>

                <div className="overflow-x-auto">
                    <table className="min-w-full text-sm">
                        <thead>
                        <tr className="border-b">
                            <th className="text-left p-2">IBAN</th>
                            <th className="text-left p-2">First</th>
                            <th className="text-left p-2">Last</th>
                            <th className="text-left p-2">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {loading ? (
                            <tr><td colSpan={4} className="p-3">Loading…</td></tr>
                        ) : users.length === 0 ? (
                            <tr><td colSpan={4} className="p-3">No users.</td></tr>
                        ) : (
                            users.map((u, i) => {
                                const fn = u.ownerData?.firstName ?? "";
                                const ln = u.ownerData?.lastName ?? "";
                                const mid = u.ownerData?.middleName ?? "";
                                const regAsString = (u as any).registrationId || (u as any).registrationID || "-";
                                return (
                                    <tr key={i} className="border-b">
                                        <td className="p-2">{u.iban}</td>
                                        <td className="p-2">{fn}</td>
                                        <td className="p-2">{ln}</td>
                                        <td className="p-2">
                                            <button className="px-3 py-1 rounded bg-gray-200" onClick={() => {
                                                setModalUser({
                                                    registrationId: String(regAsString),
                                                    iban: u.iban,
                                                    firstName: fn,
                                                    middleName: mid,
                                                    lastName: ln,
                                                });
                                                setModalOpen(true);
                                            }}>
                                                Open
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })
                        )}
                        </tbody>
                    </table>
                </div>

                <div className="pt-2">
                    <Pagination total={total} limit={limit} offset={offset} onPageChange={(page) => setOffset(page * limit)} />
                </div>
            </section>

            <UserModal open={modalOpen} onClose={() => setModalOpen(false)} user={modalUser} />
        </div>
    );
}
